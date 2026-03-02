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

    fun movePiece(
        from: Position,
        to: Position,
        currentPlayer: Player
    ): Boolean {

        if (!from.isValid() || !to.isValid()) return false

        val piece = getPiece(from) ?: return false

        if (piece.player != currentPlayer) return false

        if (getPiece(to) != null) return false

        val rowDiff = to.row - from.row
        val colDiff = to.col - from.col

        val absRow = kotlin.math.abs(rowDiff)
        val absCol = kotlin.math.abs(colDiff)

        // Must move diagonally
        if (absCol != absRow) return false

        // Forced capture rule
        if (hasCaptureForPlayer(currentPlayer) && absRow == 1) {
            return false
        }

        // NORMAL MOVE (1 step)
        if (absRow == 1) {

            if (!piece.isKing) {
                when (piece.player) {
                    Player.WHITE -> if (rowDiff != -1) return false
                    Player.BLACK -> if (rowDiff != 1) return false
                }
            }

            // Promotion check
            val promotedPiece =
                if (!piece.isKing &&
                    ((piece.player == Player.WHITE && to.row == 0) ||
                            (piece.player == Player.BLACK && to.row == 7))
                ) {
                    piece.copy(isKing = true)
                } else {
                    piece
                }

            setPiece(to, promotedPiece)
            setPiece(from, null)

            return true
        }

        // CAPTURE MOVE (2 steps)
        if (absRow == 2) {

            val middleRow = from.row + rowDiff / 2
            val middleCol = from.col + colDiff / 2
            val middlePiece = grid[middleRow][middleCol]

            if (middlePiece == null) return false
            if (middlePiece.player == piece.player) return false

            if (!piece.isKing) {
                when (piece.player) {
                    Player.WHITE -> if (rowDiff != -2) return false
                    Player.BLACK -> if (rowDiff != 2) return false
                }
            }

            // Remove captured piece
            grid[middleRow][middleCol] = null

            // Promotion check
            val promotedPiece =
                if (!piece.isKing &&
                    ((piece.player == Player.WHITE && to.row == 0) ||
                            (piece.player == Player.BLACK && to.row == 7))
                ) {
                    piece.copy(isKing = true)
                } else {
                    piece
                }

            setPiece(to, promotedPiece)
            setPiece(from, null)

            return true
        }

        return false

        // Perform move
        setPiece(to, piece)
        setPiece(from, null)

        return true
    }

    fun getGrid(): Array<Array<Piece?>> {
        return grid
    }

    fun hasCaptureForPlayer(player: Player): Boolean {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = grid[row][col] ?: continue
                if (piece.player == player) {
                    if (canCaptureFrom(Position(row, col))) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun canCaptureFrom(position: Position): Boolean {

        val piece = getPiece(position) ?: return false

        val directions = listOf(
            Pair(2, 2),
            Pair(2, -2),
            Pair(-2, 2),
            Pair(-2, -2)
        )

        for ((rowOffset, colOffset) in directions) {

            val target = Position(position.row + rowOffset, position.col + colOffset)
            val middle = Position(position.row + rowOffset / 2, position.col + colOffset / 2)

            if (!target.isValid()) continue

            val middlePiece = getPiece(middle) ?: continue
            val targetPiece = getPiece(target)

            if (targetPiece == null && middlePiece.player != piece.player) {

                if (!piece.isKing) {
                    if (piece.player == Player.WHITE && rowOffset != -2) continue
                    if (piece.player == Player.BLACK && rowOffset != 2) continue
                }

                return true
            }
        }

        return false
    }
}