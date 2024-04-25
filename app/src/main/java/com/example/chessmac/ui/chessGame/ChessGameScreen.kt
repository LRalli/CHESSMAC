package com.example.chessmac.ui.chessGame

import android.content.res.Configuration
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
import com.example.chessmac.ui.board.ChessBoard
import com.example.chessmac.ui.board.ChessBoardListener
import com.example.chessmac.ui.GameHistory.GameHistory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chessmac.ui.board.DummyChessBoardListener
import com.example.chessmac.ui.board.PieceOnSquare
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ChessGameScreen(
    mode: String,
    chessGameViewModel: ChessGameViewModel = viewModel(),
) {
    val game by chessGameViewModel.uiState.collectAsStateWithLifecycle()
    val gameStarted by chessGameViewModel.gameStarted.collectAsState()

    val checkmateEvent by chessGameViewModel.checkmateEvent.collectAsState()
    val quizEvent by chessGameViewModel.quizEvent.collectAsState()
    val currentSideToMove = chessGameViewModel.currentSideToMove


    if (checkmateEvent) {
        chessGameViewModel.showCheckmateDialog() // Reset the state for future events
        ShowCheckmateDialog(
            winner = if (currentSideToMove == "BLACK") "White" else "Black",
            onClose = { Log.d("ChessGameScreen", "Dialog dismissed") },
            mode = mode,
            points = chessGameViewModel.earnedPoints
        )
    }

    if (quizEvent) {
        chessGameViewModel.showQuizDialog() // Reset the state for future events
        ShowQuizDialog(onClose = { Log.d("ChessGameScreen", "Dialog dismissed") },
                        attempts = chessGameViewModel.quizAttempts)
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
                            .padding(top = 8.dp)
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
                            .padding(top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startQuiz()
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
                                viewModel.startQuiz()
                            },
                            enabled = viewModel.quizAttempts == 0,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        ) {
                            Text(text = "Next quiz")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(text = "Quiz Left: ${viewModel.quizLeft}")
                        Text(text = "Quiz score: ${viewModel.quizScore}")
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
                            .padding(top = 8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.startStockGame()
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

    if (dialogState.value) {
        if(mode == "LOCAL"){
            AlertDialog(
                onDismissRequest = {
                    dialogState.value = false
                    onClose()
                },
                title = { Text("Game Over") },
                text = { Text("$winner won!") },
                confirmButton = {
                    Button(onClick = {
                        dialogState.value = false
                        onClose()
                    }) {
                        Text("OK")
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
                title = { Text("Quiz solved") },
                text = { Text("You earned ${points} points") },
                confirmButton = {
                    Button(onClick = {
                        dialogState.value = false
                        onClose()
                    }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ShowQuizDialog(onClose: () -> Unit, attempts: Int) {
    val dialogState = remember { mutableStateOf(true) }

    if (dialogState.value) {
        AlertDialog(
            onDismissRequest = {
                dialogState.value = false
                onClose()
            },
            title = { Text("Bad move") },
            text = { Text("$attempts tentatives left!") },
            confirmButton = {
                Button(onClick = {
                    dialogState.value = false
                    onClose()
                }) {
                    Text("OK")
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun DebugChessBoard(listener: ChessBoardListener) {
    Log.i("DebugChessBoard", "Recomposed with listener: $listener")
}