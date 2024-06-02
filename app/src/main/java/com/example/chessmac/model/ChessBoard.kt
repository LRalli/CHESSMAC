package com.example.chessmac.model

import com.github.bhlangonijr.chesslib.Square

private const val BOARD_SIZE = 8

//Class that provides functionalities to retrieve information about squares on the chessboard.

class ChessBoard {
    val size: Int
        get() = BOARD_SIZE

    //Allows accessing a specific square on the chessboard by its row and column indices.
    operator fun get(row: Int, column: Int): Square {

        if ((row !in 0 until BOARD_SIZE) || (column !in 0 until BOARD_SIZE)) {
            return Square.NONE
        }

        return Square.values()[(size * size) - (row + 1) * size + column]
    }

    //Returns the row index of the given square on the chessboard.
    fun getRow(square: Square): Int {
        val index = Square.values().indexOfFirst { it == square }
        return size - (index / size) - 1
    }

    //Returns the column index of the given square on the chessboard.
    fun getColumn(square: Square): Int {
        val index = Square.values().indexOfFirst { it == square }

        return index % size
    }
}