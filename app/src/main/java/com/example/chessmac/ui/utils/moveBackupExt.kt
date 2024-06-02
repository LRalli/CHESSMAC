package com.example.chessmac.ui.utils

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Square

//Functions to determine if a given move is a long castle move (queenside castling)
// or a short castle move (kingside castling).
fun MoveBackup.isLongCastleMove(): Boolean {
    return this.isCastleMove && (this.move.to == Square.C1 || this.move.to == Square.C8)
}

fun MoveBackup.isShortCastleMove(): Boolean {
    return this.isCastleMove && (this.move.to == Square.G1 || this.move.to == Square.G8)
}