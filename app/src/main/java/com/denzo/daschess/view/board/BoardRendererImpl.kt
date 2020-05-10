package com.denzo.daschess.view.board

import android.content.Context
import android.text.Selection
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.denzo.daschess.R
import com.denzo.daschess.model.board.Board
import com.denzo.daschess.view.CoordinateMapper
import com.denzo.daschess.view.piece.PieceRenderer


class BoardRendererImpl(
    context: Context,
    private val pieceRenderer: PieceRenderer,
    private val coordinateMapper: CoordinateMapper,
    private val selection: Selection
) : BoardRenderer {

    private val boardImageView = ImageView(context)
    private val highlightImageView = ImageView(context)

    init {
        boardImageView.setImageResource(R.drawable.board)
        highlightImageView.setImageResource(R.drawable.selection_highlight_border)
    }

    override fun draw(board: Board, constraintLayout: ConstraintLayout) {

        constraintLayout.removeAllViews()
        constraintLayout.addView(boardImageView)

        for (rowIter in 0 until 8) {
            for (colIter in 0 until 8) {

                val gridCoordinates = Pair(rowIter, colIter)
                val piece = board.getGridElement(gridCoordinates)

                pieceRenderer.draw(constraintLayout, gridCoordinates, piece)

            }
        }

        val selectedGridLocation: Pair<Int, Int>? = selection.get()

        if (selectedGridLocation != null) {

            val boardSquareSideLength = coordinateMapper.getBoardSquareSideLength().toInt()
            val pixelCoordinates = coordinateMapper.mapGridCoordinatesToPixelCoordinates(selectedGridLocation)

            constraintLayout.addView(highlightImageView)

            highlightImageView.layoutParams.height = boardSquareSideLength
            highlightImageView.layoutParams.width = boardSquareSideLength

            highlightImageView.x = pixelCoordinates.second
            highlightImageView.y = pixelCoordinates.first

        }

    }

}