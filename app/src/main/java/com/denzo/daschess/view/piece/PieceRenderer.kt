package com.denzo.daschess.view.piece

import androidx.constraintlayout.widget.ConstraintLayout
import com.denzo.daschess.model.piece.Piece


interface PieceRenderer {

    fun draw(constraintLayout: ConstraintLayout, gridCoordinates: Pair<Int, Int>, piece: Piece?)

}