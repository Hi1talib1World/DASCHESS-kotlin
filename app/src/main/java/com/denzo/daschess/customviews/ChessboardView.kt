package com.denzo.daschess.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.denzo.daschess.R

class ChessboardView(context: Context, attrs: AttributeSet): View(context, attrs) {

    interface OnSquareSelectedListener {
        fun onSquareSelected(row: Int, col: Int)
        fun onMoveAttempted(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int)
    }

    var listener: OnSquareSelectedListener? = null

    // Color vars
    private var brightColor: Int
    private var darkColor: Int
    private var coordinateColor: Int
    private var highlightColor: Int
    private var coordinateSize: Float

    // Delta between each square
    private var delta: Int = 0

    // Vars to determine bounds of squares needed to draw
    private var selectedSquareBounds: Rect = Rect(0, 0, 0, 0)
    private var checkSquareBounds: Rect = Rect(0, 0, 0, 0)
    private var availableMovesCoordinates: List<Pair<Int, Int>> = listOf()

    // Maps to keep track of pieces
    var whitePlayerPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf()
    var blackPlayerPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf()

    // Input variables
    var currentChosenPos: Pair<Int, Int>? = null
    var lastMoveFrom: Pair<Int, Int>? = null
    var lastMoveTo: Pair<Int, Int>? = null

    // Drag and Drop variables
    private var isDragging = false
    private var dragPiece: Pair<String, Int>? = null // Name, Color (-1 or 1)
    private var dragStartPos: Pair<Int, Int>? = null
    private var dragCurrentPoint = PointF()

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
        context.theme.obtainStyledAttributes(attrs, R.styleable.ChessboardView, 0, 0).apply {
            try {
                brightColor = getColor(R.styleable.ChessboardView_brightColor, ContextCompat.getColor(context, R.color.brightSquare))
                darkColor = getColor(R.styleable.ChessboardView_darkColor, ContextCompat.getColor(context, R.color.darkSquare))
                coordinateColor = getColor(R.styleable.ChessboardView_coordinateColor, ContextCompat.getColor(context, R.color.chess_text_secondary))
                highlightColor = getColor(R.styleable.ChessboardView_moveHighlightColor, ContextCompat.getColor(context, R.color.selectionColor))
                coordinateSize = getDimension(R.styleable.ChessboardView_coordinateTextSize, 12f * resources.displayMetrics.scaledDensity)
            } finally {
                recycle()
            }
        }
    }

    private val darkPaint = Paint(ANTI_ALIAS_FLAG).apply { color = darkColor }
    private val brightPaint = Paint(ANTI_ALIAS_FLAG).apply { color = brightColor }
    private val selectedPaint = Paint(ANTI_ALIAS_FLAG).apply { color = highlightColor; alpha = 150 }
    
    private val coordinatePaintBright = Paint(ANTI_ALIAS_FLAG).apply { 
        color = brightColor
        textSize = coordinateSize
        style = Paint.Style.FILL
        isFakeBoldText = true
    }
    private val coordinatePaintDark = Paint(ANTI_ALIAS_FLAG).apply { 
        color = darkColor
        textSize = coordinateSize
        style = Paint.Style.FILL
        isFakeBoldText = true
    }

    private val highlightCirclePaint = Paint(ANTI_ALIAS_FLAG).apply { color = highlightColor; alpha = 100 }
    private val highlightCapturePaint = Paint(ANTI_ALIAS_FLAG).apply { color = highlightColor; alpha = 120; strokeWidth = 6f; style = Paint.Style.STROKE }
    private val checkPaint = Paint(ANTI_ALIAS_FLAG).apply { color = Color.RED; alpha = 100 }
    private val lastMovePaint = Paint(ANTI_ALIAS_FLAG).apply { color = Color.YELLOW; alpha = 80 }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        
        // Use the smaller dimension to keep it square and fitting
        val size = if (width > 0 && height > 0) Math.min(width, height) else width.coerceAtLeast(height)
        
        delta = size / 8
        val finalSize = delta * 8
        setMeasuredDimension(finalSize, finalSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawCoordinates(canvas)
        drawLastMove(canvas)
        drawCheck(canvas)
        drawAvailableMoves(canvas)
        drawSelection(canvas)
        drawPieces(canvas)
        drawDraggedPiece(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (i in 0..7) {
            for (j in 0..7) {
                val paint = if ((i + j) % 2 == 0) brightPaint else darkPaint
                canvas.drawRect((i * delta).toFloat(), (j * delta).toFloat(), ((i + 1) * delta).toFloat(), ((j + 1) * delta).toFloat(), paint)
            }
        }
    }

    private fun drawCoordinates(canvas: Canvas) {
        val files = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
        val ranks = arrayOf("8", "7", "6", "5", "4", "3", "2", "1")
        
        val offset = 4f * resources.displayMetrics.density

        for (i in 0..7) {
            // Draw ranks (numbers 1-8) on the first column (file 'a')
            val rankPaint = if (i % 2 == 0) coordinatePaintDark else coordinatePaintBright
            canvas.drawText(ranks[i], offset, (i * delta).toFloat() + coordinateSize + offset, rankPaint)
            
            // Draw files (letters a-h) on the last row (rank '1')
            val filePaint = if ((i + 7) % 2 == 0) coordinatePaintDark else coordinatePaintBright
            val textWidth = filePaint.measureText(files[i])
            canvas.drawText(files[i], (i + 1) * delta - textWidth - offset, (8 * delta).toFloat() - offset, filePaint)
        }
    }

    private fun drawLastMove(canvas: Canvas) {
        lastMoveFrom?.let { canvas.drawRect(transformToRect(it.second, it.first), lastMovePaint) }
        lastMoveTo?.let { canvas.drawRect(transformToRect(it.second, it.first), lastMovePaint) }
    }

    private fun drawCheck(canvas: Canvas) {
        if (checkSquareBounds != Rect(0, 0, 0, 0)) {
            canvas.drawRect(checkSquareBounds, checkPaint)
        }
    }

    private fun drawSelection(canvas: Canvas) {
        if (selectedSquareBounds != Rect(0, 0, 0, 0)) {
            canvas.drawRect(selectedSquareBounds, selectedPaint)
        }
    }

    private fun drawAvailableMoves(canvas: Canvas) {
        for (coord in availableMovesCoordinates) {
            val isOccupied = whitePlayerPieces.values.any { it.second == coord } || blackPlayerPieces.values.any { it.second == coord }
            val rect = transformToRect(coord.second, coord.first)
            if (isOccupied) {
                canvas.drawCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), (delta / 2f) - 8f, highlightCapturePaint)
            } else {
                canvas.drawCircle(rect.centerX().toFloat(), rect.centerY().toFloat(), (delta / 6f), highlightCirclePaint)
            }
        }
    }

    private fun drawPieces(canvas: Canvas) {
        for (piece in whitePlayerPieces.values) {
            if (isDragging && dragPiece?.second == -1 && piece.second == dragStartPos) continue
            whiteDrawables[piece.first]?.apply {
                bounds = transformToRect(piece.second.second, piece.second.first)
                draw(canvas)
            }
        }
        for (piece in blackPlayerPieces.values) {
            if (isDragging && dragPiece?.second == 1 && piece.second == dragStartPos) continue
            val rect = transformToRect(piece.second.second, piece.second.first)
            blackDrawables[piece.first]?.apply {
                bounds = rect
                draw(canvas)
            }
        }
    }

    private fun drawDraggedPiece(canvas: Canvas) {
        if (isDragging && dragPiece != null) {
            val drawables = if (dragPiece!!.second == -1) whiteDrawables else blackDrawables
            drawables[dragPiece!!.first]?.apply {
                val x = dragCurrentPoint.x.toInt()
                val y = dragCurrentPoint.y.toInt()
                bounds = Rect(x - delta / 2, y - delta / 2, x + delta / 2, y + delta / 2)
                draw(canvas)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val col = (x / delta).toInt()
        val row = (y / delta).toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (row in 0..7 && col in 0..7) {
                    val pieceAtStart = getPieceAt(row, col)
                    if (pieceAtStart != null) {
                        isDragging = true
                        dragPiece = pieceAtStart
                        dragStartPos = Pair(row, col)
                        dragCurrentPoint.set(x, y)
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        listener?.onSquareSelected(row, col)
                    } else {
                        listener?.onSquareSelected(row, col)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    dragCurrentPoint.set(x, y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    isDragging = false
                    if (row in 0..7 && col in 0..7 && dragStartPos != null) {
                        if (Pair(row, col) != dragStartPos) {
                            listener?.onMoveAttempted(dragStartPos!!.first, dragStartPos!!.second, row, col)
                        }
                    }
                    dragPiece = null
                    dragStartPos = null
                    invalidate()
                }
            }
        }
        return true
    }

    private fun getPieceAt(row: Int, col: Int): Pair<String, Int>? {
        whitePlayerPieces.values.find { it.second == Pair(row, col) }?.let { return Pair(it.first, -1) }
        blackPlayerPieces.values.find { it.second == Pair(row, col) }?.let { return Pair(it.first, 1) }
        return null
    }

    private fun transformToRect(col: Int, row: Int): Rect {
        return Rect(col * delta, row * delta, (col + 1) * delta, (row + 1) * delta)
    }

    fun displaySelection(row: Int, col: Int) {
        currentChosenPos = Pair(row, col)
        selectedSquareBounds = transformToRect(col, row)
        invalidate()
    }

    fun displayAvailableMoves(moves: List<Pair<Int, Int>>) {
        availableMovesCoordinates = moves
        invalidate()
    }

    fun clearSelection() {
        availableMovesCoordinates = listOf()
        selectedSquareBounds = Rect(0, 0, 0, 0)
        currentChosenPos = null
        invalidate()
    }

    fun displayCheckSquare(pos: Pair<Int, Int>?) {
        checkSquareBounds = if (pos != null) transformToRect(pos.second, pos.first) else Rect(0, 0, 0, 0)
        invalidate()
    }

    fun setLastMove(from: Pair<Int, Int>?, to: Pair<Int, Int>?) {
        lastMoveFrom = from
        lastMoveTo = to
        invalidate()
    }

    fun redrawPieces(white: MutableMap<Int, Pair<String, Pair<Int, Int>>>, black: MutableMap<Int, Pair<String, Pair<Int, Int>>>) {
        whitePlayerPieces = white
        blackPlayerPieces = black
        invalidate()
    }
}