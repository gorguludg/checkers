package com.gorguludg.checkers.game.logic

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class GameEngine {

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
        } else {
            currentPlayer = nextPlayer
        }
    }

    @SuppressLint("NewApi")
    fun undoMove() {

        if (history.isEmpty()) return

        val snapshot = history.removeLast()

        board = snapshot.board
        currentPlayer = snapshot.currentPlayer
        winner = snapshot.winner

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
    }
}