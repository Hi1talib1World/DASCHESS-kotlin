package com.denzo.daschess.model.piece

import com.denzo.daschess.model.board.Board


class Pawn(override val colour: Colour) : Piece() {

    override val pointValue = 1

    override fun getPossibleMoveSquares(board: Board, gridCoordinates: Pair<Int, Int>): List<Pair<Int, Int>> {

        val possibleMoveSquares = mutableListOf<Pair<Int, Int>>()

        if (colour == Colour.WHITE) {
            possibleMoveSquares.add(Pair(gridCoordinates.first - 1, gridCoordinates.second))
        } else if (colour == Colour.BLACK) {
            possibleMoveSquares.add(Pair(gridCoordinates.first + 1, gridCoordinates.second))
        }

        return possibleMoveSquares

    }

}