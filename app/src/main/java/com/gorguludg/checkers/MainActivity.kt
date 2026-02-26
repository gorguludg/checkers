package com.gorguludg.checkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gorguludg.checkers.ui.screen.GameScreen
import com.gorguludg.checkers.ui.theme.CheckersTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CheckersTheme {
                GameScreen()
            }
        }
    }
}