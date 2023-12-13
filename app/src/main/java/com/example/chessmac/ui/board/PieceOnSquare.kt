package com.example.chessmac.ui.board

import com.github.bhlangonijr.chesslib.Square
import com.example.chessmac.model.PieceType

data class PieceOnSquare(
    val id: Int,
    val pieceType: PieceType,
    val square: Square,
)