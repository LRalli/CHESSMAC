package com.example.chessmac.ui.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chessmac.model.PieceType
import kotlinx.collections.immutable.ImmutableList

//Functional interface to handle user interaction.
@Stable
fun interface PromotionPaneListener {
    fun onPromotionPieceTypeSelected(pieceType: PieceType, promotionString: String)
}

//Promotion Pane composable.
@Composable
fun PromotionPane(
    promotions: ImmutableList<PieceType>,
    cellSize: Dp,
    listener: PromotionPaneListener,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val itemModifier = Modifier
            .size(cellSize)
            .padding(6.dp)

        promotions.forEach { pieceType ->
            val promotionString = getPromotionString(pieceType)
            //Draw piece within Pane.
            PieceImage(
                pieceType = pieceType,
                modifier = itemModifier
                    .clickable { listener.onPromotionPieceTypeSelected(pieceType, promotionString) }
            )
        }
    }
}

//Map each choice of piece to a string.
private fun getPromotionString(pieceType: PieceType): String {
    return when (pieceType) {
        PieceType.QUEEN_DARK, PieceType.QUEEN_LIGHT -> "Q"
        PieceType.ROOK_DARK, PieceType.ROOK_LIGHT -> "R"
        PieceType.BISHOP_DARK, PieceType.BISHOP_LIGHT -> "B"
        PieceType.KNIGHT_DARK, PieceType.KNIGHT_LIGHT -> "N"
        else -> throw IllegalArgumentException("Unsupported piece type: $pieceType")
    }
}