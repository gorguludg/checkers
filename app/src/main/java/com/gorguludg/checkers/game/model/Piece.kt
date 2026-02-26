package com.gorguludg.checkers.game.model

data class Piece(
    val player: Player,
    val isKing: Boolean = false
)