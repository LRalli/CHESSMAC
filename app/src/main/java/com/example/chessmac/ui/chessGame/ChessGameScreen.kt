package com.example.chessmac.ui.chessGame

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.DisposableEffect
import com.example.chessmac.utils.ShakeDetector
import com.example.chessmac.ui.board.ChessBoard
import com.example.chessmac.ui.board.ChessBoardListener
import com.example.chessmac.ui.GameHistory.GameHistory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chessmac.ui.board.DummyChessBoardListener
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.chessmac.R
import com.example.chessmac.utils.Leaderboard

@Composable
fun ChessGameScreen(
    mode: String,
    chessGameViewModel: ChessGameViewModel = viewModel(),
    context: Context,
    backgroundResId: Int
) {
    val game by chessGameViewModel.uiState.collectAsStateWithLifecycle()
    val gameStarted by chessGameViewModel.gameStarted.collectAsState()

    val checkmateEvent by chessGameViewModel.checkmateEvent.collectAsState()
    val quizEvent by chessGameViewModel.quizEvent.collectAsState()
    val stockEvent by chessGameViewModel.stockEvent.collectAsState()
    val hintEvent by chessGameViewModel.hintEvent.collectAsState()
    val quizFin by chessGameViewModel.quizFin.collectAsState()

    val currentSideToMove = chessGameViewModel.currentSideToMove
    val bestMove = chessGameViewModel.bestMove
    val quizLeft = chessGameViewModel.quizLeft

    DisposableEffect(Unit) {
        //Initialize ShakeDetector with handleShake() to be triggered when a shake is detected
        val shakeDetector = ShakeDetector {
            chessGameViewModel.handleShake()
        }

        //Initialize sensorManager to access device's sensors
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Register the ShakeDetector as a listener for sensor events
        sensorManager.registerListener(
            shakeDetector,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val painter = painterResource(id = backgroundResId)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }

    if (checkmateEvent) {
        chessGameViewModel.showCheckmateDialog()
        ShowCheckmateDialog(
            winner = if (currentSideToMove == "BLACK") "White" else "Black",
            onClose = {
                      if(quizLeft == 0){
                          chessGameViewModel.showQuizFinDialog()
                      }
            },
            mode = mode,
            points = chessGameViewModel.earnedPoints
        )
    }

    if (quizEvent) {
        chessGameViewModel.showQuizDialog()
        ShowQuizDialog(onClose = { Log.d("ChessGameScreen", "Dialog dismissed") },
                        attempts = chessGameViewModel.quizAttempts)
    }

    if (stockEvent) {
        chessGameViewModel.showDifficultyDialog()
        ShowStockDifficultyDialog(
            onDifficultySelected = { difficulty ->
                chessGameViewModel.setStockDifficulty(difficulty)
            },
            onClose = {
                Log.d("ChessGameScreen", "Stock difficulty dialog dismissed")
            }
        )
    }

    if (hintEvent) {
        ShowHintDialog(onClose = { chessGameViewModel.showHintDialog() },
            hint = bestMove)
    }

    if (quizFin) {
        ShowFinDialog(){}
    }

    ChessGameScreen(mode = mode,
                    game = game,
                    listener = chessGameViewModel,
                    gameStarted = gameStarted,
                    viewModel = chessGameViewModel)
}

@Composable
fun ChessGameScreen(
    mode: String,
    game: ChessGameUIState,
    listener: ChessBoardListener,
    gameStarted: Boolean,
    viewModel: ChessGameViewModel
) {

    val customButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFF3A5730)
    )

    val effectiveListener = remember(gameStarted) {
        if (gameStarted) listener else DummyChessBoardListener
    }

    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            val squareSize = ((LocalConfiguration.current.screenWidthDp - 16) / 8).dp

            when (mode) {
                "LOCAL" -> {
                    ChessBoard(
                        chessBoard = game.board,
                        pieces = game.pieces,
                        selectedSquare = game.selectedSquare,
                        squaresForMove = game.squaresForMove,
                        promotions = game.promotions,
                        squareSize = squareSize,
                        listener = effectiveListener,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startGame()
                            },
                            enabled = !gameStarted,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Start",
                                color = Color.White,
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.resetGame()
                            },
                            enabled = gameStarted,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Reset",
                                color = Color.White,
                            )
                        }
                    }
                }
                "QUIZ" -> {
                    ChessBoard(
                        chessBoard = game.board,
                        pieces = game.pieces,
                        selectedSquare = game.selectedSquare,
                        squaresForMove = game.squaresForMove,
                        promotions = game.promotions,
                        squareSize = squareSize,
                        listener = effectiveListener,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 8.dp)
                    ) {
                        repeat(viewModel.hintCount) {
                            Image(
                                painter = painterResource(id = R.drawable.interrogation_mark_1),
                                contentDescription = "Hint",
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startQuiz()
                            },
                            enabled = !gameStarted,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Start",
                                color = Color.White,
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.startQuiz()
                            },
                            enabled = viewModel.quizAttempts == 0,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Next quiz",
                                color = Color.White,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 8.dp)
                    ) {
                        Text(text = "Quiz Left: ${viewModel.quizLeft}", style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "-", style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Quiz score: ${viewModel.quizScore}", style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily))
                    }
                }
                "STOCKGAME" -> {
                    ChessBoard(
                        chessBoard = game.board,
                        pieces = game.pieces,
                        selectedSquare = game.selectedSquare,
                        squaresForMove = game.squaresForMove,
                        promotions = game.promotions,
                        squareSize = squareSize,
                        listener = effectiveListener,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp, top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startStockGame()
                            },
                            enabled = !gameStarted,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Start",
                                color = Color.White,
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.resetGame()
                            },
                            enabled = gameStarted,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            colors = customButtonColors
                        ) {
                            Text(
                                text = "Reset",
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            GameHistory(
                history = game.history,
                modifier = Modifier
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            )
        }
    } else {
        val squareSize = ((LocalConfiguration.current.screenHeightDp - 16) / 8).dp

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp)
        ) {
            ChessBoard(
                chessBoard = game.board,
                pieces = game.pieces,
                selectedSquare = game.selectedSquare,
                squaresForMove = game.squaresForMove,
                promotions = game.promotions,
                squareSize = squareSize,
                listener = effectiveListener,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.startGame()
                        },
                        enabled = !gameStarted,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text(text = "Start")
                    }
                    Button(
                        onClick = {
                            viewModel.resetGame()
                        },
                        enabled = gameStarted,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        Text(text = "Reset")
                    }
                }

                GameHistory(
                    history = game.history,
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ShowCheckmateDialog(
    winner: String,
    onClose: () -> Unit,
    mode: String,
    points: Double
) {
    val dialogState = remember { mutableStateOf(true) }

    val buttonColors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A5730))

    if (dialogState.value) {
        if(mode == "LOCAL" || mode == "STOCKGAME"){
            AlertDialog(
                onDismissRequest = {
                    dialogState.value = false
                    onClose()
                },
                title = { Text("Game Over",
                    style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
                text = { Text("$winner won!",
                    style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
                confirmButton = {
                    Button(onClick = {
                        dialogState.value = false
                        onClose()
                    },
                        colors = buttonColors) {
                        Text("OK",
                            color = Color.White,
                            style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily))
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        } else if(mode == "QUIZ"){
            AlertDialog(
                onDismissRequest = {
                    dialogState.value = false
                    onClose()
                },
                title = { Text("Quiz solved",
                    style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
                text = { Text("You earned $points points",
                    style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
                confirmButton = {
                    Button(onClick = {
                        dialogState.value = false
                        onClose()
                    },
                        colors = buttonColors) {
                        Text("OK",
                            color = Color.White,
                            style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily))
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ShowHintDialog(
    hint: String,
    onClose: () -> Unit,
) {
    val dialogState = remember { mutableStateOf(true) }

    val buttonColors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A5730))

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = {
                dialogState.value = false
                onClose()
            },
            title = { Text("Hint",
                style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            text = { Text(hint,
                style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            confirmButton = {
                Button(onClick = {
                    dialogState.value = false
                    onClose()
                },
                    colors = buttonColors) {
                    Text("OK",
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily))
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ShowQuizDialog(onClose: () -> Unit, attempts: Int) {
    val dialogState = remember { mutableStateOf(true) }

    val buttonColors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A5730))

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = {
                dialogState.value = false
                onClose()
            },
            title = { Text("Bad move",
                style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            text = { Text("$attempts tentatives left!", style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            confirmButton = {
                Button(onClick = {
                    dialogState.value = false
                    onClose()
                },
                    colors = buttonColors
                ) {
                    Text("OK",
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily))
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ShowFinDialog(onClose: () -> Unit) {
    val dialogState = remember { mutableStateOf(true) }
    val context = LocalContext.current

    val buttonColors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A5730))

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = {
                dialogState.value = false
                onClose()
            },
            title = { Text("Congratulations, you won!",
                    style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            confirmButton = {
                Button(onClick = {
                    dialogState.value = false
                    onClose()
                    val intent = Intent(context, Leaderboard::class.java)
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                },
                    colors = buttonColors) {
                    Text("OK",
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily))
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ShowStockDifficultyDialog(
    onDifficultySelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val dialogState = remember { mutableStateOf(true) }

    val buttonColors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3A5730))

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = {
                onClose()
            },
            title = { Text("Select Stock Engine Difficulty",
                      style = TextStyle(fontSize = 18.sp, fontFamily = com.example.chessmac.customFontFamily)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val buttonModifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)

                    Button(
                        onClick = {
                            onDifficultySelected("600")
                            dialogState.value = false
                        },
                        modifier = buttonModifier,
                        colors = buttonColors
                    ) {
                        Text("Easy",
                            color = Color.White,
                            style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily)
                        )
                    }
                    Button(
                        onClick = {
                            onDifficultySelected("1300")
                            dialogState.value = false
                        },
                        modifier = buttonModifier,
                        colors = buttonColors
                    ) {
                        Text("Medium",
                            color = Color.White,
                            style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily)
                        )
                    }
                    Button(
                        onClick = {
                            onDifficultySelected("2000")
                            dialogState.value = false
                        },
                        modifier = buttonModifier,
                        colors = buttonColors
                    ) {
                        Text("Hard",
                            color = Color.White,
                            style = TextStyle(fontSize = 20.sp, fontFamily = com.example.chessmac.customFontFamily)
                        )
                    }
                }
            },
            buttons = {}, // Empty list of buttons
            modifier = Modifier.padding(16.dp)
        )
    }
}