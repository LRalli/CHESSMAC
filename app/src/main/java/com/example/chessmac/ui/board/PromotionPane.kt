package com.example.chessmac.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chessmac.model.PieceType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Stable
fun interface PromotionPaneListener {
    fun onPromotionPieceTypeSelected(pieceType: PieceType, promotionString: String)
}

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
            PieceImage(
                pieceType = pieceType,
                modifier = itemModifier
                    .clickable { listener.onPromotionPieceTypeSelected(pieceType, promotionString) }
            )
        }
    }
}

private fun getPromotionString(pieceType: PieceType): String {
    // Implement logic to convert PieceType to promotion string (e.g., "R" or "r")
    // You might need to adjust this based on your PieceType implementation.
    return when (pieceType) {
        PieceType.QUEEN_DARK, PieceType.QUEEN_LIGHT -> "Q"
        PieceType.ROOK_DARK, PieceType.ROOK_LIGHT -> "R"
        PieceType.BISHOP_DARK, PieceType.BISHOP_LIGHT -> "B"
        PieceType.KNIGHT_DARK, PieceType.KNIGHT_LIGHT -> "N"
        else -> throw IllegalArgumentException("Unsupported piece type: $pieceType")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0000FF)
@Composable
fun PromotionPanePreview() {
    PromotionPane(
        promotions = listOf(
            PieceType.QUEEN_DARK,
            PieceType.QUEEN_LIGHT,
            PieceType.ROOK_DARK,
            PieceType.KNIGHT_LIGHT
        ).toImmutableList(),
        cellSize = 78.dp,
        listener = {_, _ ->},
        modifier = Modifier
            .background(Color.White)
            .border(2.dp, color = Color.DarkGray)
    )
}