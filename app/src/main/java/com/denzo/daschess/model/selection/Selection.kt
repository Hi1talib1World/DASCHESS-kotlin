package com.denzo.daschess.model.selection

interface Selection {

    fun get(): Pair<Int, Int>?
    fun set(gridCoordinates: Pair<Int, Int>)

}