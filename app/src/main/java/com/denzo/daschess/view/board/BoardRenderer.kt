package com.denzo.daschess.view.board

import androidx.constraintlayout.widget.ConstraintLayout
import com.denzo.daschess.model.board.Board


interface BoardRenderer {

    fun draw(board: Board, constraintLayout: ConstraintLayout)

}