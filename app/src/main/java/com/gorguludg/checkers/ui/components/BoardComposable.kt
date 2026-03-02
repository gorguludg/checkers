package com.gorguludg.checkers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gorguludg.checkers.game.logic.Board
import com.gorguludg.checkers.game.model.Position
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.gorguludg.checkers.game.model.Player
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment

@Composable
fun BoardComposable(
    board: Board,
    currentPlayer: Player,
    onSquareSelected: (Position) -> Unit,
    selectedPosition: Position?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        val grid = board.getGrid()

        for (row in 0..7) {
            Row(modifier = Modifier.weight(1f)) {

                for (col in 0..7) {

                    val isDarkSquare = (row + col) % 2 != 0

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                if (selectedPosition?.row == row && selectedPosition.col == col)
                                    Color.Yellow
                                else if (isDarkSquare)
                                    Color(0xFF769656)
                                else
                                    Color(0xFFEEEED2)
                            )
                            .clickable {
                                onSquareSelected(Position(row, col))
                            }
                    ) {
                        val piece = board.getPiece(Position(row, col))

                        if (piece != null) {

                            val pieceColor =
                                if (piece.player == Player.WHITE) Color.White
                                else Color.Black

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(pieceColor),
                                contentAlignment = Alignment.Center
                            ) {

                                if (piece.isKing) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700)) // gold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}