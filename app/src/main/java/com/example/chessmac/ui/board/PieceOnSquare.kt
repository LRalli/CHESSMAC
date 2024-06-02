package com.example.chessmac.ui.board

import com.github.bhlangonijr.chesslib.Square
import com.example.chessmac.model.PieceType

//Data class used to represent a chess piece on a specific square on the chessboard.
data class PieceOnSquare(
    val id: Int,
    val pieceType: PieceType,
    val square: Square,
)