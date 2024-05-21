package com.example.chessmac.ui.board

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.github.bhlangonijr.chesslib.Square
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.example.chessmac.model.ChessBoard
import com.example.chessmac.ui.utils.toIntOffset

private const val Z_INDEX_IDLE = 0f
private const val Z_INDEX_DRAGGING = 1f
private const val SCALE_IDLE = 1f
private const val SCALE_DRAGGING = 1.5f

@Stable
interface PieceListener {
    fun onTakePiece(square: Square)
    fun onReleasePiece(square: Square)
}

@Composable
fun Piece (
    piece: PieceOnSquare,
    squareSize: Dp,
    chessBoard: ChessBoard,
    listener: PieceListener,
    dragPieceOverSquareListener: (Square) -> Unit,
    modifier: Modifier = Modifier,
) {

    val rememberedListener by rememberUpdatedState(listener)

    val squareSizePx = with(LocalDensity.current) {
        squareSize.toPx()
    }

    val row = chessBoard.getRow(piece.square)
    val column = chessBoard.getColumn(piece.square)

    val x by rememberUpdatedState(column * squareSizePx)
    val y by rememberUpdatedState(row * squareSizePx)

    var zIndex by remember { mutableStateOf(Z_INDEX_IDLE) }
    var scale by remember { mutableStateOf(SCALE_IDLE) }

    val offset = remember {
        Animatable(
            Offset(x = column * squareSizePx, y = row * squareSizePx),
            Offset.VectorConverter
        )
    }

    fun calculateSquare(): Square {

        val row = ((offset.value.y + squareSizePx / 2) / squareSizePx).toInt()
        val column = ((offset.value.x + squareSizePx / 2) / squareSizePx).toInt()

        return chessBoard[row, column]
    }

    LaunchedEffect(key1 = piece.square) {
        offset.animateTo(Offset(x, y))
    }

    PieceImage(
        pieceType = piece.pieceType,
        modifier = modifier
            .offset { offset.value.toIntOffset() }
            .zIndex(zIndex)
            .scale(scale)
            .size(squareSize)
            .pointerInput(Unit) {
                coroutineScope {
                    detectDragGestures(
                        onDragStart = {
                            zIndex = Z_INDEX_DRAGGING
                            scale = SCALE_DRAGGING
                            val square = calculateSquare()
                            rememberedListener.onTakePiece(square)
                        },

                        onDragEnd = {
                            zIndex = Z_INDEX_IDLE
                            scale = SCALE_IDLE

                            dragPieceOverSquareListener(Square.NONE)

                            val square = calculateSquare()
                            rememberedListener.onReleasePiece(square)

                            launch {
                                offset.animateTo(Offset(x, y))
                            }
                        },
                        onDragCancel = {
                            zIndex = Z_INDEX_IDLE
                            scale = SCALE_IDLE

                            dragPieceOverSquareListener(Square.NONE)

                            launch {
                                offset.animateTo(Offset(x, y))
                            }
                        }
                    )
                    { change, dragAmount ->
                        change.consume()

                        dragPieceOverSquareListener(calculateSquare())

                        launch {
                            offset.snapTo(
                                Offset(
                                    x = offset.value.x + dragAmount.x * scale,
                                    y = offset.value.y + dragAmount.y * scale,
                                )
                            )
                        }
                    }
                }
            }
    )
}