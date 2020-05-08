package com.denzo.daschess.model.board

import com.alltimeslucky.cheekychess.model.piece.Piece

interface Board {

    fun getGridElement(gridCoordinates: Pair<Int, Int>): Piece?
    fun getHeight(): Int
    fun getWidth(): Int
    fun initializeGrid()
    fun isOccupied(gridCoordinates: Pair<Int, Int>): Boolean
    fun move(firstGridCoordinates: Pair<Int, Int>, secondGridCoordinates: Pair<Int, Int>): Boolean

}