package com.denzo.daschess.controller

import com.denzo.daschess.model.selection.Selection
import com.denzo.daschess.view.CoordinateMapper


class SelectionController(private val coordinateMapper: CoordinateMapper, private val selection: Selection) {

    fun select(pixelCoordinates: Pair<Float, Float>) {

        val gridCoordinates = coordinateMapper.mapPixelCoordinatesToGridCoordinates(pixelCoordinates)
        selection.set(gridCoordinates)

    }

}