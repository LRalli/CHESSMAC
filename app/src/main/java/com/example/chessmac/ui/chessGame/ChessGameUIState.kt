package com.example.chessmac.ui.chessGame

import com.github.bhlangonijr.chesslib.Square
import com.example.chessmac.model.ChessBoard
import com.example.chessmac.model.PieceType
import com.example.chessmac.ui.board.PieceOnSquare
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

data class ChessGameUIState(
    val board: ChessBoard,
    val pieces: ImmutableList<PieceOnSquare>,
    val selectedSquare: Square?,
    val squaresForMove: ImmutableSet<Square>,
    val promotions: ImmutableList<PieceType>,
    val history: ImmutableList<String>
)