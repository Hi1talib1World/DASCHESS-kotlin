package com.denzo.daschess.customviews

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.denzo.daschess.R

class ChessboardView(context: Context, attrs: AttributeSet): View(context, attrs) {

    // Color vars
    private var brightColor: Int
    private var darkColor: Int

    // Delta between each square
    private var delta: Int = 0

    // Vars to determine bounds of squares needed to draw
    var selectedSquareBounds: Rect = Rect(0, 0, 0, 0)
    var availableMovesBounds: MutableList<Rect> = mutableListOf()

    // Maps to init positions of pieces and keep track of them
    var whitePlayerPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf(
        -1 to Pair("King", Pair(7, 3)),
        -2 to Pair("Queen", Pair(7, 4)),
        -3 to Pair("Rook", Pair(7, 0)),
        -4 to Pair("Rook", Pair(7, 7)),
        -5 to Pair("Knight", Pair(7, 1)),
        -6 to Pair("Knight", Pair(7, 6)),
        -7 to Pair("Bishop", Pair(7, 2)),
        -8 to Pair("Bishop", Pair(7, 5)),
        -9 to Pair("Pawn", Pair(6, 0)),
        -10 to Pair("Pawn", Pair(6, 1)),
        -11 to Pair("Pawn", Pair(6, 2)),
        -12 to Pair("Pawn", Pair(6, 3)),
        -13 to Pair("Pawn", Pair(6, 4)),
        -14 to Pair("Pawn", Pair(6, 5)),
        -15 to Pair("Pawn", Pair(6, 6)),
        -16 to Pair("Pawn", Pair(6, 7))
    )
    var blackPlayerPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf(
        1 to Pair("King", Pair(0, 3)),
        2 to Pair("Queen", Pair(0, 4)),
        3 to Pair("Rook", Pair(0, 0)),
        4 to Pair("Rook", Pair(0, 7)),
        5 to Pair("Knight", Pair(0, 1)),
        6 to Pair("Knight", Pair(0, 6)),
        7 to Pair("Bishop", Pair(0, 2)),
        8 to Pair("Bishop", Pair(0, 5)),
        9 to Pair("Pawn", Pair(1, 0)),
        10 to Pair("Pawn", Pair(1, 1)),
        11 to Pair("Pawn", Pair(1, 2)),
        12 to Pair("Pawn", Pair(1, 3)),
        13 to Pair("Pawn", Pair(1, 4)),
        14 to Pair("Pawn", Pair(1, 5)),
        15 to Pair("Pawn", Pair(1, 6)),
        16 to Pair("Pawn", Pair(1, 7))
    )

    // variables to handle selecting and moving pieces
    var currentChosenPos: Pair<Int, Int>? = null
    var previousChosenPos: Pair<Int, Int>? = null

    // Drawable resources for pieces
    private val drawableWhitePieces: MutableMap<Int, Drawable?> = mutableMapOf(
        -1 to AppCompatResources.getDrawable(context, R.drawable.chess_klt60),
        -2 to AppCompatResources.getDrawable(context, R.drawable.chess_qlt60),
        -3 to AppCompatResources.getDrawable(context, R.drawable.chess_rlt60),
        -4 to AppCompatResources.getDrawable(context, R.drawable.chess_rlt60),
        -5 to AppCompatResources.getDrawable(context, R.drawable.chess_nlt60),
        -6 to AppCompatResources.getDrawable(context, R.drawable.chess_nlt60),
        -7 to AppCompatResources.getDrawable(context, R.drawable.chess_blt60),
        -8 to AppCompatResources.getDrawable(context, R.drawable.chess_blt60),
        -9 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -10 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -11 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -12 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -13 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -14 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -15 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60),
        -16 to AppCompatResources.getDrawable(context, R.drawable.chess_plt60))

    private val drawableBlackPieces: MutableMap<Int, Drawable?> = mutableMapOf(
        1 to AppCompatResources.getDrawable(context, R.drawable.chess_kdt60),
        2 to AppCompatResources.getDrawable(context, R.drawable.chess_qdt60),
        3 to AppCompatResources.getDrawable(context, R.drawable.chess_rdt60),
        4 to AppCompatResources.getDrawable(context, R.drawable.chess_rdt60),
        5 to AppCompatResources.getDrawable(context, R.drawable.chess_ndt60),
        6 to AppCompatResources.getDrawable(context, R.drawable.chess_ndt60),
        7 to AppCompatResources.getDrawable(context, R.drawable.chess_bdt60),
        8 to AppCompatResources.getDrawable(context, R.drawable.chess_bdt60),
        9 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        10 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        11 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        12 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        13 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        14 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        15 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60),
        16 to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60))

    init {
        // Get access to attributes defined in xml
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChessboardView, 0, 0).apply {
            try {
                brightColor = getColor(R.styleable.ChessboardView_brightColor, Color.WHITE)
                darkColor = getColor(R.styleable.ChessboardView_darkColor, Color.LTGRAY)
            }
            finally {
                recycle()
            }
        }
    }

    // Define Paints for elements to draw
    private val darkPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.darkSquare)
        style = Paint.Style.FILL
    }

    private val brightPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.brightSquare)
    }

    private val selectedPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.selectionColor)
        alpha = 200
        style = Paint.Style.FILL
    }

    private val availableMovePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.selectionColor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        delta = measuredWidth/8
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    private fun getHostActivity(): AppCompatActivity? {
        // Helper method to gain access to parent activity
        var hostContext = context

        while (hostContext is ContextWrapper) {
            if (hostContext is AppCompatActivity) {
                return hostContext
            }
            hostContext = (context as ContextWrapper).baseContext
        }
        return null
    }

    // Helper functions to draw graphics
    private fun drawBoard(canvas: Canvas) {
        // Draw squares of chessboard
        for (i in (1..8)) {
            for (j in (1..8)) {
                if ((i+j).rem(2) == 0) canvas.drawRect(((i-1)*delta).toFloat(),
                    ((j-1)*delta).toFloat(), (i*delta).toFloat(), (j*delta).toFloat(), brightPaint)
                else canvas.drawRect(((i-1)*delta).toFloat(),
                    ((j-1)*delta).toFloat(), (i*delta).toFloat(), (j*delta).toFloat(), darkPaint)
            }
        }
    }

    private fun drawSelection(canvas: Canvas) {
        // Draw special tile for selected piece
        canvas.drawRect(selectedSquareBounds, selectedPaint)

        // Draw special tiles for available positions to move
        for (bounds in availableMovesBounds) {
            canvas.drawRect(bounds, selectedPaint)
        }
    }

    private fun drawPieces(canvas: Canvas) {
        // Draw white pieces
        for ((pieceNum, piece) in whitePlayerPieces) {
            val piecePos = piece.second
            drawableWhitePieces[pieceNum]?.apply {
                bounds = transformToRect(piecePos.second, piecePos.first)
                draw(canvas)
            }
        }

        // Draw black pieces
        for ((pieceNum, piece) in blackPlayerPieces) {
            val piecePos = piece.second
            drawableBlackPieces[pieceNum]?.apply {
                bounds = transformToRect(piecePos.second, piecePos.first)
                draw(canvas)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBoard(canvas)
        drawSelection(canvas)
        drawPieces(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                getPositionOfSquare(event.x, event.y)
                val hostActivity = getHostActivity()
                hostActivity?.onTouchEvent(event)
            }
        }
        return true
    }

    // Function to extract position of tile in my board coordinates from coordinates from touch
    private fun getPositionOfSquare(x: Float, y: Float) {
        // get number of row and column for selected square
        val rowPositionOfSquare = (y/delta).toInt()
        val colPositionOfSquare = (x/delta).toInt()

        // Store coordinates of last selected position
        previousChosenPos = currentChosenPos
        // Store coordinates of selected position on chessboard
        currentChosenPos = Pair(rowPositionOfSquare, colPositionOfSquare)
        println("Current pos: " + currentChosenPos.toString())
        println("Previous pos: " + previousChosenPos.toString())
    }

    private fun transformToRect(xPositionOfSquare: Int, yPositionOfSquare: Int): Rect {
        val yBottom = (yPositionOfSquare+1)*delta
        val xLeft = (xPositionOfSquare)*delta
        val xRight = (xPositionOfSquare+1)*delta
        val yTop = (yPositionOfSquare)*delta

        return Rect(xLeft,yTop, xRight, yBottom)
    }

    // Make square with given coordinates selected
    fun displaySelection() {
        selectedSquareBounds = transformToRect(currentChosenPos!!.second, currentChosenPos!!.first)
        this.invalidate()
    }

    fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>) {
        availableMovesBounds = mutableListOf()
        for (movePosition in movesCoordinates) {
            val xPositionOfSquare = movePosition.second
            val yPositionOfSquare = movePosition.first

            availableMovesBounds.add(transformToRect(xPositionOfSquare, yPositionOfSquare))
            this.invalidate()
        }
    }

    fun clearSelection() {
        availableMovesBounds = mutableListOf()
        selectedSquareBounds = Rect(0, 0, 0, 0)
        invalidate()
    }

    fun redrawPieces(whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                     blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>) {

        whitePlayerPieces = whitePieces
        blackPlayerPieces = blackPieces
        invalidate()
    }
}