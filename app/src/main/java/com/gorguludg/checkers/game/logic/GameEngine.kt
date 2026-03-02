package com.gorguludg.checkers.game.logic

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class GameEngine {

    enum class GameMode {
        PVP,
        PVAI
    }

    data class GameSnapshot(
        val board: Board,
        val currentPlayer: Player,
        val winner: Player?
    )

    var board by mutableStateOf(Board())
        private set

    var currentPlayer by mutableStateOf(Player.WHITE)
        private set

    var selectedPosition by mutableStateOf<Position?>(null)
        private set

    var legalMoves by mutableStateOf<List<Position>>(emptyList())
        private set

    var winner by mutableStateOf<Player?>(null)
        private set

    var gameMode by mutableStateOf(GameMode.PVP)

    var humanPlayer by mutableStateOf(Player.WHITE)

    private val history = mutableListOf<GameSnapshot>()

    fun onSquareClicked(position: Position) {

        if (winner != null) return

        if (selectedPosition == null) {

            val piece = board.getPiece(position)

            if (piece != null && piece.player == currentPlayer) {
                selectedPosition = position
                legalMoves = board.getLegalMoves(position, currentPlayer)
            }

        } else {

            val from = selectedPosition!!

            // Make a copy BEFORE mutating board
            val boardBeforeMove = board.deepCopy()

            val result = board.movePiece(from, position, currentPlayer)

            when (result) {

                Board.MoveResult.INVALID -> {
                    selectedPosition = null
                    legalMoves = emptyList()
                }

                Board.MoveResult.NORMAL -> {

                    history.add(
                        GameSnapshot(
                            board = boardBeforeMove,
                            currentPlayer = currentPlayer,
                            winner = winner
                        )
                    )

                    switchTurn()

                    selectedPosition = null
                    legalMoves = emptyList()
                }

                Board.MoveResult.CAPTURE -> {

                    val newPosition = position

                    if (board.canCaptureFrom(newPosition)) {

                        selectedPosition = newPosition
                        legalMoves = board.getLegalMoves(newPosition, currentPlayer)

                    } else {

                        history.add(
                            GameSnapshot(
                                board = boardBeforeMove,
                                currentPlayer = currentPlayer,
                                winner = winner
                            )
                        )

                        switchTurn()

                        selectedPosition = null
                        legalMoves = emptyList()
                    }
                }
            }
        }
    }

    private fun saveSnapshot() {
        history.add(
            GameSnapshot(
                board = board.deepCopy(),
                currentPlayer = currentPlayer,
                winner = winner
            )
        )
    }

    private fun switchTurn() {

        val nextPlayer = currentPlayer.opponent()

        if (!board.hasAnyPieces(nextPlayer) ||
            !board.hasAnyValidMove(nextPlayer)
        ) {
            winner = currentPlayer
            return
        }

        currentPlayer = nextPlayer

        // Trigger AI if needed
        if (gameMode == GameMode.PVAI &&
            currentPlayer != humanPlayer &&
            winner == null
        ) {
            performAIMove()
        }
    }

    private fun performAIMove() {

        val bestMove = findBestMove(currentPlayer) ?: return

        val boardBeforeMove = board.deepCopy()

        val result = board.movePiece(
            bestMove.first,
            bestMove.second,
            currentPlayer
        )

        if (result == Board.MoveResult.NORMAL) {

            history.add(
                GameSnapshot(
                    board = boardBeforeMove,
                    currentPlayer = currentPlayer,
                    winner = winner
                )
            )

            switchTurn()

        } else if (result == Board.MoveResult.CAPTURE) {

            val newPosition = bestMove.second

            if (board.canCaptureFrom(newPosition)) {
                // For simplicity, AI does not chain multi-capture yet
                switchTurn()
            } else {

                history.add(
                    GameSnapshot(
                        board = boardBeforeMove,
                        currentPlayer = currentPlayer,
                        winner = winner
                    )
                )

                switchTurn()
            }
        }
    }

    private fun findBestMove(player: Player): Pair<Position, Position>? {

        val moves = mutableListOf<Pair<Position, Position>>()

        for (row in 0..7) {
            for (col in 0..7) {

                val from = Position(row, col)
                val piece = board.getPiece(from) ?: continue
                if (piece.player != player) continue

                val legal = board.getLegalMoves(from, player)

                for (to in legal) {
                    moves.add(from to to)
                }
            }
        }

        if (moves.isEmpty()) return null

        var bestScore = Int.MIN_VALUE
        var bestMove: Pair<Position, Position>? = null

        for ((from, to) in moves) {

            val copy = board.deepCopy()
            copy.movePiece(from, to, player)

            val score = evaluateBoard(copy, player)

            if (score > bestScore) {
                bestScore = score
                bestMove = from to to
            }
        }

        return bestMove
    }

    private fun evaluateBoard(board: Board, player: Player): Int {

        var score = 0

        for (row in 0..7) {
            for (col in 0..7) {

                val piece = board.getPiece(Position(row, col)) ?: continue

                val value = if (piece.isKing) 3 else 1

                if (piece.player == player) {
                    score += value
                } else {
                    score -= value
                }
            }
        }

        return score
    }

    @SuppressLint("NewApi")
    fun undoMove() {

        if (history.isEmpty()) return

        if (gameMode == GameMode.PVAI) {

            // Remove AI move
            history.removeLastOrNull()

            // Remove human move
            if (history.isNotEmpty()) {
                val snapshot = history.removeLast()
                board = snapshot.board
                currentPlayer = snapshot.currentPlayer
                winner = snapshot.winner
            }

        } else {

            val snapshot = history.removeLast()

            board = snapshot.board
            currentPlayer = snapshot.currentPlayer
            winner = snapshot.winner
        }

        selectedPosition = null
        legalMoves = emptyList()
    }

    fun resetGame() {
        board = Board()
        currentPlayer = Player.WHITE
        selectedPosition = null
        legalMoves = emptyList()
        winner = null
        history.clear()

        // If PvAI and human is BLACK, AI (WHITE) moves first
        if (gameMode == GameMode.PVAI &&
            humanPlayer == Player.BLACK
        ) {
            performAIMove()
        }
    }
}