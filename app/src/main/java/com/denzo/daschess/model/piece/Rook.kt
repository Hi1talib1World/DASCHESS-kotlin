package com.denzo.daschess.model.piece

import com.denzo.daschess.model.board.Board


class Rook(override val colour: Colour) : Piece() {

    override val pointValue = 5

    override fun getPossibleMoveSquares(board: Board, gridCoordinates: Pair<Int, Int>): List<Pair<Int, Int>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}