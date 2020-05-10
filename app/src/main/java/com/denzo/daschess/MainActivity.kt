package com.denzo.daschess

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.alltimeslucky.cheekychess.controller.SelectionController
import com.alltimeslucky.cheekychess.koin.Module
import com.alltimeslucky.cheekychess.model.board.Board
import com.alltimeslucky.cheekychess.view.CoordinateMapper
import com.alltimeslucky.cheekychess.view.board.BoardRenderer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import android.os.Bundle
import com.denzo.daschess.koin.Module

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
