package com.gorguludg.checkers.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gorguludg.checkers.game.logic.GameEngine
import com.gorguludg.checkers.ui.components.BoardComposable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp

@Composable
fun GameScreen() {

    val engine = remember { GameEngine() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (engine.winner != null) {
                Text(text = "Winner: ${engine.winner}")
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(text = "Turn: ${engine.currentPlayer}")
                Spacer(modifier = Modifier.height(16.dp))
            }

            BoardComposable(
                board = engine.board,
                currentPlayer = engine.currentPlayer,
                selectedPosition = engine.selectedPosition,
                onSquareSelected = { position ->
                    engine.onSquareClicked(position)
                }
            )
        }
    }
}