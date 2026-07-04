package com.denzo.daschess

import kotlin.math.abs

class Player(var color: Int) {
    // -1 = white, 1 = black
    private val initialRowPos = if (color == 1) 0 else 7

    // Number of fig -> Name, Position
    val pieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf(
        1 * color to Pair("King", Pair(initialRowPos, 4)),
        2 * color to Pair("Queen", Pair(initialRowPos, 3)),
        3 * color to Pair("Rook", Pair(initialRowPos, 0)),
        4 * color to Pair("Rook", Pair(initialRowPos, 7)),
        5 * color to Pair("Knight", Pair(initialRowPos, 1)),
        6 * color to Pair("Knight", Pair(initialRowPos, 6)),
        7 * color to Pair("Bishop", Pair(initialRowPos, 2)),
        8 * color to Pair("Bishop", Pair(initialRowPos, 5))
    )

    var availableMoves = mutableMapOf<Int, List<Pair<Int, Int>>>()
    val pieceMoveCounts = mutableMapOf<Int, Int>()

    init {
        for (i in 0..7) {
            pieces[(i + 9) * color] = Pair("Pawn", Pair(initialRowPos + color, i))
        }
    }

    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.pieces.clear()
        newPlayer.pieces.putAll(pieces)
        newPlayer.availableMoves.clear()
        newPlayer.availableMoves.putAll(availableMoves)
        newPlayer.pieceMoveCounts.clear()
        newPlayer.pieceMoveCounts.putAll(pieceMoveCounts)
        return newPlayer
    }

    // function to update available moves for all pieces of this Player
    fun updateAvailableMoves(
        board: Array<IntArray>,
        lastMoveCurrent: Pair<Int, Int>? = null,
        lastMovePrevious: Pair<Int, Int>? = null,
        lastMovedPiece: Int = 0
    ): Unit {

        // before updating available moves clear moves for previous state
        availableMoves = mutableMapOf()

        fun checkForObstacle(currentPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
            val currentFig = board[currentPos.first][currentPos.second]
            val nextFig = board[nextPos.first][nextPos.second]
            
            val currentSign = if (currentFig > 0) 1 else if (currentFig < 0) -1 else 0
            val nextSign = if (nextFig > 0) 1 else if (nextFig < 0) -1 else 0
            
            return (currentFig == 0 || currentSign == color) &&
                    (nextFig == 0 || nextSign == -color)
        }

        fun checkIfOnBoard(pos: Pair<Int, Int>): Boolean = (0..7).contains(pos.first) && (0..7).contains(pos.second)

        fun fKing(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            val rows = ((pos.first - 1)..(pos.first + 1)).toList().filter{(checkIfOnBoard(Pair(it, pos.second)))}
            val cols = ((pos.second - 1)..(pos.second + 1)).toList().filter{(checkIfOnBoard(Pair(pos.first, it)))}

            rows.forEach{row ->
                cols.forEach{col ->
                    if (Pair(row, col) != pos) {
                        val fig = board[row][col]
                        val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                        if (fig == 0 || figSign == -color) {
                            positions += Pair(row, col)
                        }
                    }
                }}

            // Castling logic
            val kingNum = 1 * color
            if ((pieceMoveCounts[kingNum] ?: 0) == 0) {
                // Kingside Castling
                val rookKingsideNum = 4 * color
                if ((pieceMoveCounts[rookKingsideNum] ?: 0) == 0) {
                    if (board[initialRowPos][5] == 0 && board[initialRowPos][6] == 0) {
                        positions += Pair(initialRowPos, 6)
                    }
                }
                // Queenside Castling
                val rookQueensideNum = 3 * color
                if ((pieceMoveCounts[rookQueensideNum] ?: 0) == 0) {
                    if (board[initialRowPos][1] == 0 && board[initialRowPos][2] == 0 && board[initialRowPos][3] == 0) {
                        positions += Pair(initialRowPos, 2)
                    }
                }
            }
            return positions
        }

        fun fRook(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            arrayOf(0, 7).forEach{endPoint ->
                run {
                    val order = if (endPoint == 0) -1 else 1
                    var row = pos.first
                    while ((row * order < endPoint) &&
                        checkForObstacle(Pair(row, pos.second), Pair(row+order, pos.second))) {
                        row += order
                        positions += Pair(row, pos.second)
                        if (board[row][pos.second] != 0) break
                    }
                }}
            arrayOf(0, 7).forEach { endPoint ->
                run {
                    val order = if (endPoint == 0) -1 else 1
                    var col = pos.second
                    while ((col * order < endPoint) &&
                        checkForObstacle(Pair(pos.first, col), Pair(pos.first, col+order))){
                        col += order
                        positions += Pair(pos.first, col)
                        if (board[pos.first][col] != 0) break
                    }
                }
            }
            return positions
        }

        fun fBishop(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            fun scan(dr: Int, dc: Int) {
                var r = pos.first + dr
                var c = pos.second + dc
                while (checkIfOnBoard(Pair(r, c))) {
                    if (checkForObstacle(Pair(r - dr, c - dc), Pair(r, c))) {
                        positions += Pair(r, c)
                        if (board[r][c] != 0) break
                    } else break
                    r += dr
                    c += dc
                }
            }
            scan(1, 1); scan(1, -1); scan(-1, 1); scan(-1, -1)
            return positions
        }

        fun fQueen(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            return fBishop(pos).union(fRook(pos)).toMutableList()
        }

        fun fKnight(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            arrayOf(Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1), Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2)).forEach {
                val p = Pair(pos.first + it.first, pos.second + it.second)
                if (checkIfOnBoard(p)) {
                    val fig = board[p.first][p.second]
                    val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                    if (figSign != color) positions += p
                }
            }
            return positions
        }

        fun fPawn(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val movePositions = mutableListOf<Pair<Int, Int>>()
            val nextRow = pos.first + color
            if (checkIfOnBoard(Pair(nextRow, pos.second)) && board[nextRow][pos.second] == 0) {
                movePositions += Pair(nextRow, pos.second)
                val startRow = initialRowPos + color
                if (pos.first == startRow && board[pos.first + 2 * color][pos.second] == 0) {
                    movePositions += Pair(pos.first + 2 * color, pos.second)
                }
            }
            val attacks = listOf(Pair(nextRow, pos.second - 1), Pair(nextRow, pos.second + 1))
            for (p in attacks) {
                if (checkIfOnBoard(p)) {
                    val fig = board[p.first][p.second]
                    val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                    if (figSign == -color) movePositions += p
                }
            }
            if (lastMoveCurrent != null && lastMovePrevious != null) {
                val lastPieceSign = if (lastMovedPiece > 0) 1 else if (lastMovedPiece < 0) -1 else 0
                if (lastPieceSign == -color && abs(lastMoveCurrent.first - lastMovePrevious.first) == 2) {
                    if (lastMoveCurrent.first == pos.first && abs(lastMoveCurrent.second - pos.second) == 1) {
                        movePositions += Pair(pos.first + color, lastMoveCurrent.second)
                    }
                }
            }
            return movePositions
        }

        fun applyFunction(name: String, pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            return when (name) {
                "King" -> fKing(pos)
                "Queen" -> fQueen(pos)
                "Rook" -> fRook(pos)
                "Knight" -> fKnight(pos)
                "Bishop" -> fBishop(pos)
                "Pawn" -> fPawn(pos)
                else -> mutableListOf()
            }
        }

        for ((pieceNum, piece) in pieces) {
            availableMoves[pieceNum] = applyFunction(piece.first, piece.second)
        }
    }
}