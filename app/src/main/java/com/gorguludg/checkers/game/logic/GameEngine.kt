package com.gorguludg.checkers.game.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class GameEngine {

    var board by mutableStateOf(Board())
        private set

    var currentPlayer by mutableStateOf(Player.WHITE)
        private set

    var selectedPosition by mutableStateOf<Position?>(null)
        private set

    var legalMoves by mutableStateOf<List<Position>>(emptyList())
        private set

    fun onSquareClicked(position: Position) {

        if (winner != null) return

        if (selectedPosition == null) {

            val piece = board.getPiece(position)

            if (piece != null && piece.player == currentPlayer) {
                selectedPosition = position
                legalMoves = board.getLegalMoves(position, currentPlayer)
            }

        } else {

            val result = board.movePiece(
                from = selectedPosition!!,
                to = position,
                currentPlayer = currentPlayer
            )

            when (result) {

                Board.MoveResult.INVALID -> {
                    selectedPosition = null
                    legalMoves = emptyList()
                }

                Board.MoveResult.NORMAL -> {
                    val nextPlayer = currentPlayer.opponent()

                    if (!board.hasAnyPieces(nextPlayer) ||
                        !board.hasAnyValidMove(nextPlayer)
                    ) {
                        winner = currentPlayer
                    } else {
                        currentPlayer = nextPlayer
                    }
                    selectedPosition = null
                    legalMoves = emptyList()
                }

                Board.MoveResult.CAPTURE -> {

                    val newPosition = position

                    if (board.canCaptureFrom(newPosition)) {
                        selectedPosition = newPosition
                    } else {
                        val nextPlayer = currentPlayer.opponent()

                        if (!board.hasAnyPieces(nextPlayer) ||
                            !board.hasAnyValidMove(nextPlayer)
                        ) {
                            winner = currentPlayer
                        } else {
                            currentPlayer = nextPlayer
                        }
                        selectedPosition = null
                        legalMoves = emptyList()
                    }
                }
            }
        }
    }

    var winner by mutableStateOf<Player?>(null)
        private set

    fun resetGame() {
        board = Board()
        currentPlayer = Player.WHITE
        selectedPosition = null
        winner = null
    }

}