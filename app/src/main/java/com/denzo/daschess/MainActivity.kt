package com.denzo.daschess

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.denzo.daschess.controller.SelectionController
import com.denzo.daschess.koin.Module
import com.denzo.daschess.model.board.Board
import com.denzo.daschess.view.CoordinateMapper
import com.denzo.daschess.view.board.BoardRenderer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(Module.appModule)
        }

        setContentView(R.layout.activity_main)

        val board: Board by inject()
        board.initializeGrid()

        val boardRenderer: BoardRenderer by inject()
        val constraintLayout = findViewById<ConstraintLayout>(R.id.mainLayout)

        val coordinateMapper: CoordinateMapper by inject()
        val selectionController: SelectionController by inject()

        constraintLayout.setOnTouchListener { _, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {

                val layoutPixelColumn = motionEvent.x
                val layoutPixelRow = motionEvent.y

                selectionController.select(Pair(layoutPixelRow, layoutPixelColumn))
                boardRenderer.draw(board, constraintLayout)

            }

            true

        }

        val vto = constraintLayout.viewTreeObserver

        vto.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    constraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val layoutHeight: Int = constraintLayout.measuredHeight
                    val layoutWidth: Int = constraintLayout.measuredWidth

                    coordinateMapper.layoutHeight = layoutHeight
                    coordinateMapper.layoutWidth = layoutWidth

                    boardRenderer.draw(board, constraintLayout)

                }
            }
        )

    }

}