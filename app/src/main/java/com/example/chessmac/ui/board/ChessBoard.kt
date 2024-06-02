package com.example.chessmac.ui.board

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Square
import com.example.chessmac.model.ChessBoard
import com.example.chessmac.model.PieceType
import com.example.chessmac.ui.theme.Copper
import kotlinx.collections.immutable.*

//Interface which defines methods for handling different events related to the chessboard.
@Stable
interface ChessBoardListener : PromotionPaneListener, PieceListener {
    fun onSquareClicked(square: Square)
}

//Interface used to as placeholder when the game is not started.

object DummyChessBoardListener : ChessBoardListener {
    override fun onSquareClicked(square: Square) {}
    override fun onTakePiece(square: Square) {}
    override fun onReleasePiece(square: Square) {}
    override fun onPromotionPieceTypeSelected(pieceType: PieceType, promotionString: String) {}
}

//Composable used to display Chessboard UI.

@Composable
fun ChessBoard(
    chessBoard: ChessBoard,                   //Board instance
    pieces: ImmutableList<PieceOnSquare>,     //Pieces on board
    selectedSquare: Square?,                  //Currently selected square
    squaresForMove: ImmutableSet<Square>,     //List of valid moves from picked square
    promotions: ImmutableList<PieceType>,     //List of possible promotions
    squareSize: Dp,                           //Size of squares
    listener: ChessBoardListener,             //Listener to handle user interactions
    modifier: Modifier = Modifier             //Modifier for composable
) {

    //Used to trigger changes in listener between Dummy and actual Chessboard listener
    val rememberedListener by rememberUpdatedState(listener)

    //Handle square size based on screen density
    val squareSizePx = with(LocalDensity.current) {
        squareSize.toPx()
    }

    //state holder that can be updated and observed by the composable, initialized with empty square.
    var dragOverSquare by remember { mutableStateOf(Square.NONE) }
    //Lambda function that changes the state holder.
    val dragPieceOverSquareListener: (Square) -> Unit = { dragOverSquare = it }

    //Box composable to draw chessboard.
    Box(
        modifier = modifier
            .size(squareSize * 8)
            //Cached to reduce redundant draws.
            .drawWithCache {
                val size = Size(squareSizePx, squareSizePx)

                //Execute drawing commands.
                onDrawBehind {
                    drawSquares(chessBoard, squareSizePx, size)
                    drawSelectedSquare(chessBoard, selectedSquare, squareSizePx, size)
                    drawSquaresForPossibleMoves(chessBoard, squaresForMove, squareSizePx)
                    drawDragOverSquare(chessBoard, dragOverSquare, squareSizePx)
                }
            }
            .border(2.dp, color = Color.Black)
            //Configure box to handle pointer input events.
            .pointerInput(Unit) {
                //Detects tap gestures, upon which it retrieves square relative to tapped position and calls method.
                detectTapGestures { offset ->
                    val row = (offset.y / squareSizePx).toInt()
                    val column = (offset.x / squareSizePx).toInt()
                    val square = chessBoard[row, column]
                    rememberedListener.onSquareClicked(square)
                }
            }
    ) {
        //Contents of box composable from here.
        //Pieces to place on board.
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
        //Promotion pane UI.
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

//Below functions handle board drawing. They are called by onDrawBehind block within drawWithCache.
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