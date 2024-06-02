package com.example.chessmac.ui.board

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.chessmac.R
import com.example.chessmac.model.PieceType

//Composable responsible for displaying the image of a chess piece based on its type.
@Composable
fun PieceImage(
    pieceType: PieceType,
    modifier: Modifier,
) {
    Image(
        modifier = modifier,
        contentScale = ContentScale.Fit,
        painter = painterResource(getPieceTypeImageRes(pieceType)),
        contentDescription = stringResource(getPieceTypeDescriptionRes(pieceType))
    )
}

//Helper function to map a PieceType to a corresponding drawable resource.
private fun getPieceTypeImageRes(pieceType: PieceType): Int =
    when (pieceType) {
        PieceType.KING_LIGHT -> R.drawable.king_light
        PieceType.QUEEN_LIGHT -> R.drawable.queen_light
        PieceType.ROOK_LIGHT -> R.drawable.rook_light
        PieceType.BISHOP_LIGHT -> R.drawable.bishop_light
        PieceType.KNIGHT_LIGHT -> R.drawable.knight_light
        PieceType.PAWN_LIGHT -> R.drawable.pawn_light
        PieceType.KING_DARK -> R.drawable.king_dark
        PieceType.QUEEN_DARK -> R.drawable.queen_dark
        PieceType.ROOK_DARK -> R.drawable.rook_dark
        PieceType.BISHOP_DARK -> R.drawable.bishop_dark
        PieceType.KNIGHT_DARK -> R.drawable.knight_dark
        PieceType.PAWN_DARK -> R.drawable.pawn_dark
    }

//Helper function to map a PieceType to a corresponding string resource for accessibility.
private fun getPieceTypeDescriptionRes(pieceType: PieceType): Int =
    when (pieceType) {
        PieceType.KING_LIGHT -> R.string.king_light
        PieceType.QUEEN_LIGHT -> R.string.queen_light
        PieceType.ROOK_LIGHT -> R.string.rook_light
        PieceType.BISHOP_LIGHT -> R.string.bishop_light
        PieceType.KNIGHT_LIGHT -> R.string.knight_light
        PieceType.PAWN_LIGHT -> R.string.pawn_light
        PieceType.KING_DARK -> R.string.king_dark
        PieceType.QUEEN_DARK -> R.string.queen_dark
        PieceType.ROOK_DARK -> R.string.rook_dark
        PieceType.BISHOP_DARK -> R.string.bishop_dark
        PieceType.KNIGHT_DARK -> R.string.knight_dark
        PieceType.PAWN_DARK -> R.string.pawn_dark
    }