package com.gorguludg.checkers.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gorguludg.checkers.game.logic.GameEngine
import com.gorguludg.checkers.ui.components.BoardComposable
import com.gorguludg.checkers.game.model.Player

@Composable
fun GameScreen() {

    val engine = remember { GameEngine() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Row {
                Button(onClick = { engine.gameMode = GameEngine.GameMode.PVP
                    engine.resetGame() }) {
                    Text("PvP")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { engine.gameMode = GameEngine.GameMode.PVAI
                    engine.resetGame() }) {
                    Text("PvAI")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (engine.gameMode == GameEngine.GameMode.PVAI) {

                Row {
                    Button(onClick = { engine.humanPlayer = Player.WHITE
                        engine.resetGame()
                    }) {
                        Text("Play White")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { engine.humanPlayer = Player.BLACK
                        engine.resetGame()
                    }) {
                        Text("Play Black")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (engine.winner != null) {
                Text(text = "Winner: ${engine.winner}")
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(text = "Turn: ${engine.currentPlayer}")
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row {
                Button(onClick = { engine.undoMove() }) {
                    Text("Undo")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { engine.resetGame() }) {
                    Text("Restart")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BoardComposable(
                board = engine.board,
                currentPlayer = engine.currentPlayer,
                selectedPosition = engine.selectedPosition,
                legalMoves = engine.legalMoves,
                onSquareSelected = { position ->
                    engine.onSquareClicked(position)
                }
            )
        }
    }
}