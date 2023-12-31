package com.example.chessmac.ui.board

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Square
import com.example.chessmac.model.ChessBoard
import com.example.chessmac.model.PieceType
import com.example.chessmac.ui.theme.Copper
import kotlinx.collections.immutable.*

@Stable
interface ChessBoardListener : PromotionPaneListener, PieceListener {
    fun onSquareClicked(square: Square)
}

object DummyChessBoardListener : ChessBoardListener {
    override fun onSquareClicked(square: Square) {}
    override fun onTakePiece(square: Square) {}
    override fun onReleasePiece(square: Square) {}
    override fun onPromotionPieceTypeSelected(pieceType: PieceType) {}
}

@Composable
fun ChessBoard(
    chessBoard: ChessBoard,
    pieces: ImmutableList<PieceOnSquare>,
    selectedSquare: Square?,
    squaresForMove: ImmutableSet<Square>,
    promotions: ImmutableList<PieceType>,
    squareSize: Dp,
    listener: ChessBoardListener,
    modifier: Modifier = Modifier
) {

    // Ensures that rememberedListener is updated when listener changes
    val rememberedListener by rememberUpdatedState(listener)

    val squareSizePx = with(LocalDensity.current) {
        squareSize.toPx()
    }
//The remember function is used to remember the state across recompositions in Jetpack Compose.
    var dragOverSquare by remember { mutableStateOf(Square.NONE) }
    val dragPieceOverSquareListener: (Square) -> Unit = { dragOverSquare = it }


    Box(
        modifier = modifier
            .size(squareSize * 8)
            .drawWithCache {
                val size = Size(squareSizePx, squareSizePx)

                onDrawBehind {
                    drawSquares(chessBoard, squareSizePx, size)
                    drawSelectedSquare(chessBoard, selectedSquare, squareSizePx, size)
                    drawSquaresForPossibleMoves(chessBoard, squaresForMove, squareSizePx)
                    drawDragOverSquare(chessBoard, dragOverSquare, squareSizePx)
                }
            }
            .border(2.dp, color = Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val row = (offset.y / squareSizePx).toInt()
                    val column = (offset.x / squareSizePx).toInt()
                    val square = chessBoard[row, column]
                    rememberedListener.onSquareClicked(square)
                }
            }
    ) {
        pieces.forEach { piece ->
            key(piece.id) {
                Piece(
                    piece = piece,
                    squareSize = squareSize,
                    chessBoard = chessBoard,
                    dragPieceOverSquareListener = dragPieceOverSquareListener,
                    listener = rememberedListener,
                )
            }
        }

        if (promotions.isNotEmpty()) {
            PromotionPane(
                promotions = promotions,
                cellSize = squareSize * 1.75f,
                listener = rememberedListener,
                modifier = Modifier
                    .background(Color.White)
                    .border(2.dp, color = Color.DarkGray)
                    .align(Alignment.Center)
            )
        }
    }
}

private fun DrawScope.drawSquares(chessBoard: ChessBoard, squareSizePx: Float, size: Size) {
    for (row in 0 until chessBoard.size) {
        for (column in 0 until chessBoard.size) {
            drawRect(
                color = if (chessBoard[row, column].isLightSquare) Color.White else Copper,
                topLeft = Offset(x = column * squareSizePx, y = row * squareSizePx),
                size = size
            )
        }
    }
}

private fun DrawScope.drawSelectedSquare(
    chessBoard: ChessBoard,
    selectedSquare: Square?,
    squareSizePx: Float,
    size: Size,
) {
    if (selectedSquare != null) {
        val row = chessBoard.getRow(selectedSquare)
        val column = chessBoard.getColumn(selectedSquare)

        drawRect(
            color = Color.Yellow.copy(alpha = 0.6f),
            topLeft = Offset(x = column * squareSizePx, y = row * squareSizePx),
            size = size
        )
    }
}

private fun DrawScope.drawSquaresForPossibleMoves(
    chessBoard: ChessBoard,
    squaresForMove: Set<Square>,
    squareSizePx: Float,
) {
    squaresForMove.forEach { square ->
        val row = chessBoard.getRow(square)
        val column = chessBoard.getColumn(square)

        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            center = Offset(
                x = column * squareSizePx + squareSizePx / 2,
                y = row * squareSizePx + squareSizePx / 2
            ),
            radius = (squareSizePx) / 3
        )
    }
}

private fun DrawScope.drawDragOverSquare(
    chessBoard: ChessBoard,
    square: Square,
    squareSizePx: Float,
) {
    if (square == Square.NONE) {
        return
    }

    val row = chessBoard.getRow(square)
    val column = chessBoard.getColumn(square)

    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        center = Offset(
            x = column * squareSizePx + squareSizePx / 2,
            y = row * squareSizePx + squareSizePx / 2
        ),
        radius = (squareSizePx) / 1.4f
    )
}

//The @Preview annotation indicates that this composable function is intended for previewing in the design tool of Android Studio.
@Preview(showBackground = true, backgroundColor = 0xFF0000FF)
@Composable
fun ChessBoardPreview() {
    ChessBoard(
        chessBoard = ChessBoard(),
        pieces = persistentListOf(
            PieceOnSquare(0, PieceType.PAWN_LIGHT, Square.A2),
            PieceOnSquare(1, PieceType.PAWN_DARK, Square.A7),
            PieceOnSquare(2, PieceType.ROOK_DARK, Square.A8),
            PieceOnSquare(3, PieceType.ROOK_LIGHT, Square.A1),
            PieceOnSquare(4, PieceType.KNIGHT_LIGHT, Square.G1),
        ),
        selectedSquare = Square.C2,
        squaresForMove = persistentSetOf(Square.A1, Square.A3, Square.B4, Square.D4, Square.E2),
        promotions = emptyList<PieceType>().toImmutableList(),
        squareSize = 48.dp,
        //The listener object provides empty implementations for the ChessBoardListener interface methods.
        // This is sufficient for preview purposes as actual interaction logic is not needed for a static UI preview.
        listener = object : ChessBoardListener {
            override fun onSquareClicked(square: Square) {}
            override fun onTakePiece(square: Square) {}
            override fun onReleasePiece(square: Square) {}
            override fun onPromotionPieceTypeSelected(pieceType: PieceType) {}
        },
    )
}