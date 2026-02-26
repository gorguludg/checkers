package com.gorguludg.checkers.game.logic

import com.gorguludg.checkers.game.model.Piece
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class Board {

    private val grid: Array<Array<Piece?>> =
        Array(8) { Array<Piece?>(8) { null } }

    init {
        setupInitialPosition()
    }

    private fun setupInitialPosition() {
        for (row in 0..7) {
            for (col in 0..7) {

                // Only dark squares are used in checkers
                if ((row + col) % 2 != 0) {

                    when (row) {
                        in 0..2 -> {
                            grid[row][col] = Piece(Player.BLACK)
                        }
                        in 5..7 -> {
                            grid[row][col] = Piece(Player.WHITE)
                        }
                    }
                }
            }
        }
    }

    fun getPiece(position: Position): Piece? {
        if (!position.isValid()) return null
        return grid[position.row][position.col]
    }

    fun setPiece(position: Position, piece: Piece?) {
        if (!position.isValid()) return
        grid[position.row][position.col] = piece
    }

    fun getGrid(): Array<Array<Piece?>> {
        return grid
    }
}