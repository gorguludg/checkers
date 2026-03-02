package com.gorguludg.checkers.game.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gorguludg.checkers.game.model.Player
import com.gorguludg.checkers.game.model.Position

class GameEngine {

    val board = Board()

    var currentPlayer by mutableStateOf(Player.WHITE)
        private set

    var selectedPosition by mutableStateOf<Position?>(null)
        private set

    fun onSquareClicked(position: Position) {

        if (selectedPosition == null) {

            val piece = board.getPiece(position)

            if (piece != null && piece.player == currentPlayer) {
                selectedPosition = position
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
                }

                Board.MoveResult.NORMAL -> {
                    currentPlayer = currentPlayer.opponent()
                    selectedPosition = null
                }

                Board.MoveResult.CAPTURE -> {

                    val newPosition = position

                    if (board.canCaptureFrom(newPosition)) {
                        selectedPosition = newPosition
                    } else {
                        currentPlayer = currentPlayer.opponent()
                        selectedPosition = null
                    }
                }
            }
        }
    }
}