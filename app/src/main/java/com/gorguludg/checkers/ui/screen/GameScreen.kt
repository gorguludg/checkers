package com.gorguludg.checkers.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gorguludg.checkers.game.logic.GameEngine
import com.gorguludg.checkers.ui.components.BoardComposable

@Composable
fun GameScreen() {

    val engine = remember { GameEngine() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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