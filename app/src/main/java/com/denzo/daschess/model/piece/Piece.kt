package com.denzo.daschess.model.piece


import com.denzo.daschess.model.board.Board

abstract class Piece {

    abstract val colour: Colour
    protected abstract val pointValue: Int

    abstract fun getPossibleMoveSquares(board: Board, gridCoordinates: Pair<Int, Int>): List<Pair<Int, Int>>

}