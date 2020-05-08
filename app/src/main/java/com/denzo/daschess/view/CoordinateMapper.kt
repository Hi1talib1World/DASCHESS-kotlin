package com.denzo.daschess.view

import com.denzo.daschess.model.board.Board


class CoordinateMapper(private val board: Board) {

    private var boardSquareSideLength: Float = 0F
    private var topAndBottomExcess: Float = 0F

    var layoutHeight: Int = 0
    var layoutWidth: Int = 0
        set(value) {
            field = value
            boardSquareSideLength = value.toFloat() / board.getWidth().toFloat()
            topAndBottomExcess = (layoutHeight - layoutWidth) / 2F
        }

    fun getBoardSquareSideLength(): Float {

        return boardSquareSideLength

    }

    fun mapGridCoordinatesToPixelCoordinates(gridCoordinates: Pair<Int, Int>): Pair<Float, Float> {

        val startPixelRow = topAndBottomExcess + boardSquareSideLength * gridCoordinates.first
        val startPixelCol = boardSquareSideLength * gridCoordinates.second

        return Pair(startPixelRow, startPixelCol)

    }

    fun mapPixelCoordinatesToGridCoordinates(pixelCoordinates: Pair<Float, Float>): Pair<Int, Int> {

        val gridRow = (pixelCoordinates.first - topAndBottomExcess) / boardSquareSideLength
        val gridCol = pixelCoordinates.second / boardSquareSideLength

        return Pair(gridRow.toInt(), gridCol.toInt())

    }

}