package com.example.chessmac.ui.board

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

//Interface to handle user interactions on pieces.
@Stable
interface PieceListener {
    fun onTakePiece(square: Square)
    fun onReleasePiece(square: Square)
}

//Composable that represent a single piece on the board.
@Composable
fun Piece (
    piece: PieceOnSquare,                               //Piece to be displayed.
    squareSize: Dp,                                     //Size of square containing it.
    chessBoard: ChessBoard,                             //Board on which to display it.
    listener: PieceListener,                            //To handle user interactions.
    dragPieceOverSquareListener: (Square) -> Unit,      //Lambda function to update square when piece is dragged over it.
    modifier: Modifier = Modifier,                      //Modifier for composable.
) {

    //Used to trigger changes in listener.
    val rememberedListener by rememberUpdatedState(listener)

    //Handle square size based on screen density
    val squareSizePx = with(LocalDensity.current) {
        squareSize.toPx()
    }

    //Get row and column indexes of square on the chessboard.
    val row = chessBoard.getRow(piece.square)
    val column = chessBoard.getColumn(piece.square)

    //RememberStateUpdate to handle changes of x and y for the square.
    val x by rememberUpdatedState(column * squareSizePx)
    val y by rememberUpdatedState(row * squareSizePx)

    //MutableState to handle changes of zIndex and scale of the piece.
    var zIndex by remember { mutableStateOf(Z_INDEX_IDLE) }
    var scale by remember { mutableStateOf(SCALE_IDLE) }

    //Animatable to handle animation of piece's offset.
    val offset = remember {
        Animatable(
            Offset(x = column * squareSizePx, y = row * squareSizePx),
            Offset.VectorConverter
        )
    }

    //Fun to calculate square based on current offset value.
    fun calculateSquare(): Square {

        val row = ((offset.value.y + squareSizePx / 2) / squareSizePx).toInt()
        val column = ((offset.value.x + squareSizePx / 2) / squareSizePx).toInt()

        return chessBoard[row, column]
    }

    //Effect to animate piece to its initial position when the composable is first launched/when the piece's square changes.
    LaunchedEffect(key1 = piece.square) {
        offset.animateTo(Offset(x, y))
    }

    //PieceImage composable to display Image of piece
    PieceImage(
        pieceType = piece.pieceType,
        modifier = modifier
            .offset { offset.value.toIntOffset() }
            .zIndex(zIndex)
            .scale(scale)
            .size(squareSize)
            //PointerInput to detect drag gestures.
            .pointerInput(Unit) {
                //use of coroutine to handle animations and state updates asynchronously,
                // so that the main thread doesn't freeze.
                coroutineScope {
                    //Used to detect drag gestures, provides callbacks for different stages of drag process.
                    detectDragGestures(
                        //Invoked when drag gesture starts.
                        onDragStart = {
                            zIndex = Z_INDEX_DRAGGING
                            scale = SCALE_DRAGGING
                            val square = calculateSquare()
                            rememberedListener.onTakePiece(square)
                        },
                        //Invoked when drag gestue ends.
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
                        //Invoked if drag gesture is cancelled.
                        onDragCancel = {
                            zIndex = Z_INDEX_IDLE
                            scale = SCALE_IDLE

                            dragPieceOverSquareListener(Square.NONE)

                            launch {
                                offset.animateTo(Offset(x, y))
                            }
                        }
                    )
                    //lambda function invoked repeatedly as the drag gesture progresses.
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