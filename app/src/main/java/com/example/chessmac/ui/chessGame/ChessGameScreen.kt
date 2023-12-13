package com.example.chessmac.ui.chessGame

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@Composable
fun ChessGameScreen(
    chessGameViewModel: ChessGameViewModel = viewModel()
) {
    val game by chessGameViewModel.uiState.collectAsStateWithLifecycle()
    val gameStarted by chessGameViewModel.gameStarted.collectAsState()

    ChessGameScreen(game = game,
                    listener = chessGameViewModel,
                    gameStarted = gameStarted,
                    viewModel = chessGameViewModel)
}

@Composable
fun ChessGameScreen(
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
            DebugChessBoard(effectiveListener)
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
            GameHistory(
                history = game.history,
                modifier = Modifier
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp)
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
fun DebugChessBoard(listener: ChessBoardListener) {
    Log.i("DebugChessBoard", "Recomposed with listener: $listener")
}