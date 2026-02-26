package com.gorguludg.checkers.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gorguludg.checkers.game.logic.Board
import com.gorguludg.checkers.ui.components.BoardComposable

@Composable
fun GameScreen() {

    val board = Board()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BoardComposable(board = board)
    }
}