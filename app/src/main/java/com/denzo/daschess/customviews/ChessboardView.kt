package com.denzo.daschess.customviews

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
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
    private var coordinateColor: Int
    private var highlightColor: Int
    private var coordinateSize: Float

    // Delta between each square
    private var delta: Int = 0
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    // Vars to determine bounds of squares needed to draw
    var selectedSquareBounds: Rect = Rect(0, 0, 0, 0)
    var checkSquareBounds: Rect = Rect(0, 0, 0, 0)
    var availableMovesCoordinates: List<Pair<Int, Int>> = listOf()

    // Maps to init positions of pieces and keep track of them
    var whitePlayerPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf(
        -1 to Pair("King", Pair(7, 4)),
        -2 to Pair("Queen", Pair(7, 3)),
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
        1 to Pair("King", Pair(0, 4)),
        2 to Pair("Queen", Pair(0, 3)),
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
    
    // variables to highlight last move
    var lastMoveFrom: Pair<Int, Int>? = null
    var lastMoveTo: Pair<Int, Int>? = null

    // Drawable resources for pieces (mapped by name)
    private val whiteDrawables: Map<String, Drawable?> = mapOf(
        "King" to AppCompatResources.getDrawable(context, R.drawable.chess_klt60),
        "Queen" to AppCompatResources.getDrawable(context, R.drawable.chess_qlt60),
        "Rook" to AppCompatResources.getDrawable(context, R.drawable.chess_rlt60),
        "Bishop" to AppCompatResources.getDrawable(context, R.drawable.chess_blt60),
        "Knight" to AppCompatResources.getDrawable(context, R.drawable.chess_nlt60),
        "Pawn" to AppCompatResources.getDrawable(context, R.drawable.chess_plt60)
    )

    private val blackDrawables: Map<String, Drawable?> = mapOf(
        "King" to AppCompatResources.getDrawable(context, R.drawable.chess_kdt60),
        "Queen" to AppCompatResources.getDrawable(context, R.drawable.chess_qdt60),
        "Rook" to AppCompatResources.getDrawable(context, R.drawable.chess_rdt60),
        "Bishop" to AppCompatResources.getDrawable(context, R.drawable.chess_bdt60),
        "Knight" to AppCompatResources.getDrawable(context, R.drawable.chess_ndt60),
        "Pawn" to AppCompatResources.getDrawable(context, R.drawable.chess_pdt60)
    )

    init {
        // Get access to attributes defined in xml
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChessboardView, 0, 0).apply {
            try {
                brightColor = getColor(R.styleable.ChessboardView_brightColor, ContextCompat.getColor(context, R.color.brightSquare))
                darkColor = getColor(R.styleable.ChessboardView_darkColor, ContextCompat.getColor(context, R.color.darkSquare))
                coordinateColor = getColor(R.styleable.ChessboardView_coordinateColor, ContextCompat.getColor(context, R.color.chess_text_secondary))
                highlightColor = getColor(R.styleable.ChessboardView_moveHighlightColor, ContextCompat.getColor(context, R.color.selectionColor))
                coordinateSize = getDimension(R.styleable.ChessboardView_coordinateTextSize, 12f * resources.displayMetrics.scaledDensity)
            }
            finally {
                recycle()
            }
        }
    }

    // Define Paints for elements to draw
    private val darkPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = darkColor
        style = Paint.Style.FILL
    }

    private val brightPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = brightColor
        style = Paint.Style.FILL
    }

    private val selectedPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = highlightColor
        alpha = 150
        style = Paint.Style.FILL
    }

    private val coordinatePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = coordinateColor
        textSize = coordinateSize
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val highlightCirclePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = highlightColor
        alpha = 100
        style = Paint.Style.FILL
    }

    private val highlightCapturePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = highlightColor
        alpha = 120
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val checkPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        alpha = 100
        style = Paint.Style.FILL
    }

    private val lastMovePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        alpha = 80
        style = Paint.Style.FILL
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
            hostContext = hostContext.baseContext
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

    private fun drawCoordinates(canvas: Canvas) {
        val files = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
        val ranks = arrayOf("8", "7", "6", "5", "4", "3", "2", "1")

        for (i in 0..7) {
            // Files (a-h) at the bottom
            canvas.drawText(files[i], (i * delta + delta - 12).toFloat(), (8 * delta - 8).toFloat(), coordinatePaint)
            // Ranks (1-8) at the left
            canvas.drawText(ranks[i], 12f, (i * delta + 24).toFloat(), coordinatePaint)
        }
    }

    private fun drawSelection(canvas: Canvas) {
        // Draw last move highlights
        lastMoveFrom?.let { canvas.drawRect(transformToRect(it.second, it.first), lastMovePaint) }
        lastMoveTo?.let { canvas.drawRect(transformToRect(it.second, it.first), lastMovePaint) }

        // Draw red highlight for King in check
        if (checkSquareBounds != Rect(0, 0, 0, 0)) {
            canvas.drawRect(checkSquareBounds, checkPaint)
        }

        // Draw special tile for selected piece
        canvas.drawRect(selectedSquareBounds, selectedPaint)

        // Draw modern highlights for available moves
        for (coord in availableMovesCoordinates) {
            val isOccupied = isSquareOccupied(coord)
            val rect = transformToRect(coord.second, coord.first)
            
            if (isOccupied) {
                // Bracket style for captures
                val padding = 8f
                canvas.drawCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), (delta/2f) - padding, highlightCapturePaint)
            } else {
                // Small dot for empty squares
                canvas.drawCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), (delta/6f), highlightCirclePaint)
            }
        }
    }

    private fun isSquareOccupied(coord: Pair<Int, Int>): Boolean {
        return whitePlayerPieces.values.any { it.second == coord } || 
               blackPlayerPieces.values.any { it.second == coord }
    }

    private fun drawPieces(canvas: Canvas) {
        // Draw white pieces
        for (piece in whitePlayerPieces.values) {
            val pieceName = piece.first
            val piecePos = piece.second
            whiteDrawables[pieceName]?.apply {
                bounds = transformToRect(piecePos.second, piecePos.first)
                draw(canvas)
            }
        }

        // Draw black pieces
        for (piece in blackPlayerPieces.values) {
            val pieceName = piece.first
            val piecePos = piece.second
            blackDrawables[pieceName]?.apply {
                bounds = transformToRect(piecePos.second, piecePos.first)
                draw(canvas)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBoard(canvas)
        drawCoordinates(canvas)
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

        if (rowPositionOfSquare in 0..7 && colPositionOfSquare in 0..7) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        // Store coordinates of last selected position
        previousChosenPos = currentChosenPos
        // Store coordinates of selected position on chessboard
        currentChosenPos = Pair(rowPositionOfSquare, colPositionOfSquare)
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
        if (currentChosenPos != null) {
            selectedSquareBounds = transformToRect(currentChosenPos!!.second, currentChosenPos!!.first)
            this.invalidate()
        }
    }

    fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>) {
        availableMovesCoordinates = movesCoordinates
        this.invalidate()
    }

    fun clearSelection() {
        availableMovesCoordinates = listOf()
        selectedSquareBounds = Rect(0, 0, 0, 0)
        invalidate()
    }

    fun displayCheckSquare(pos: Pair<Int, Int>?) {
        checkSquareBounds = if (pos != null) {
            transformToRect(pos.second, pos.first)
        } else {
            Rect(0, 0, 0, 0)
        }
        invalidate()
    }

    fun setLastMove(from: Pair<Int, Int>?, to: Pair<Int, Int>?) {
        lastMoveFrom = from
        lastMoveTo = to
        if (from != null && to != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
        }
        invalidate()
    }

    fun redrawPieces(whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                     blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>) {

        whitePlayerPieces = whitePieces
        blackPlayerPieces = blackPieces
        invalidate()
    }
}