package com.gorguludg.checkers.game.model

enum class Player {
    WHITE,
    BLACK;

    fun opponent(): Player {
        return if (this == WHITE) BLACK else WHITE
    }
}