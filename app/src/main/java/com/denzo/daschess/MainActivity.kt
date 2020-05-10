package com.denzo.daschess

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.support.constraint.ConstraintLayout
import android.view.ViewTreeObserver.OnGlobalLayoutListener
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(Module.appModule)
        }
        setContentView(R.layout.activity_main)
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
