package com.gorguludg.checkers.game.logic

import com.gorguludg.checkers.game.model.Piece
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class Board {

    enum class MoveResult {
        INVALID,
        NORMAL,
        CAPTURE
    }

    private val grid: Array<Array<Piece?>> =
        Array(8) { Array<Piece?>(8) { null } }

    init {
        setupInitialPosition()
    }

    private fun setupInitialPosition() {
        for (row in 0..7) {
            for (col in 0..7) {
                if ((row + col) % 2 != 0) {
                    when (row) {
                        in 0..2 -> grid[row][col] = Piece(Player.BLACK)
                        in 5..7 -> grid[row][col] = Piece(Player.WHITE)
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
    ): MoveResult {

        if (!from.isValid() || !to.isValid()) return MoveResult.INVALID

        val piece = getPiece(from) ?: return MoveResult.INVALID

        if (piece.player != currentPlayer) return MoveResult.INVALID

        if (getPiece(to) != null) return MoveResult.INVALID

        val rowDiff = to.row - from.row
        val colDiff = to.col - from.col

        val absRow = kotlin.math.abs(rowDiff)
        val absCol = kotlin.math.abs(colDiff)

        if (absCol != absRow) return MoveResult.INVALID

        if (hasCaptureForPlayer(currentPlayer) && absRow == 1) {
            return MoveResult.INVALID
        }

        // NORMAL MOVE
        if (absRow == 1) {

            if (!piece.isKing) {
                when (piece.player) {
                    Player.WHITE -> if (rowDiff != -1) return MoveResult.INVALID
                    Player.BLACK -> if (rowDiff != 1) return MoveResult.INVALID
                }
            }

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

            return MoveResult.NORMAL
        }

        // CAPTURE MOVE
        if (absRow == 2) {

            val middleRow = from.row + rowDiff / 2
            val middleCol = from.col + colDiff / 2
            val middlePiece = grid[middleRow][middleCol]

            if (middlePiece == null) return MoveResult.INVALID
            if (middlePiece.player == piece.player) return MoveResult.INVALID

            if (!piece.isKing) {
                when (piece.player) {
                    Player.WHITE -> if (rowDiff != -2) return MoveResult.INVALID
                    Player.BLACK -> if (rowDiff != 2) return MoveResult.INVALID
                }
            }

            grid[middleRow][middleCol] = null

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

            return MoveResult.CAPTURE
        }

        return MoveResult.INVALID
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

    fun canCaptureFrom(position: Position): Boolean {

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

    fun hasAnyValidMove(player: Player): Boolean {

        for (row in 0..7) {
            for (col in 0..7) {

                val piece = grid[row][col] ?: continue
                if (piece.player != player) continue

                val from = Position(row, col)

                // Try all possible destinations
                for (r in -2..2) {
                    for (c in -2..2) {

                        val to = Position(row + r, col + c)
                        if (!to.isValid()) continue

                        val result = movePiecePreview(from, to, player)
                        if (result != MoveResult.INVALID) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    private fun movePiecePreview(
        from: Position,
        to: Position,
        player: Player
    ): MoveResult {

        val backupFrom = getPiece(from)
        val backupTo = getPiece(to)

        val middleBackup =
            if (kotlin.math.abs(to.row - from.row) == 2)
                getPiece(Position((from.row + to.row) / 2, (from.col + to.col) / 2))
            else null

        val result = movePiece(from, to, player)

        // Undo move
        setPiece(from, backupFrom)
        setPiece(to, backupTo)

        if (middleBackup != null) {
            val middle = Position((from.row + to.row) / 2, (from.col + to.col) / 2)
            setPiece(middle, middleBackup)
        }

        return result
    }

    fun hasAnyPieces(player: Player): Boolean {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = grid[row][col]
                if (piece?.player == player) return true
            }
        }
        return false
    }
}