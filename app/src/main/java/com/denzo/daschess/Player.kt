package com.denzo.daschess

import kotlin.math.sign

class Player(var color: Int) {
    // -1 = white, 1 = black
    private val initialRowPos = if (color == 1) 0 else 7

    // TODO: make Piece object and replace all pair in maps to them

    // Number of fig -> Name, Position
    val pieces: MutableMap<Int, Pair<String, Pair<Int, Int>>> = mutableMapOf(
        1 * color to Pair("King", Pair(initialRowPos, 3)),
        2 * color to Pair("Queen", Pair(initialRowPos, 4)),
        3 * color to Pair("Rook", Pair(initialRowPos, 0)),
        4 * color to Pair("Rook", Pair(initialRowPos, 7)),
        5 * color to Pair("Knight", Pair(initialRowPos, 1)),
        6 * color to Pair("Knight", Pair(initialRowPos, 6)),
        7 * color to Pair("Bishop", Pair(initialRowPos, 2)),
        8 * color to Pair("Bishop", Pair(initialRowPos, 5))
    )

    var availableMoves = mutableMapOf<Int, List<Pair<Int, Int>>>()

    init {
        for (i in 0..7) {
            pieces[(i + 9) * color] = Pair("Pawn", Pair(initialRowPos + color, i))
        }
    }

    // function to update available moves for all pieces of this Player
    fun updateAvailableMoves(board: Array<IntArray>): Unit {

        // before updating available moves clear moves for previous state
        availableMoves = mutableMapOf()

        // Check fig positions for several cases:
        //
        // First: current position is occupied by player's side, next by other side or empty
        // Second: current position is empty and next is occupied by other side or empty
        //
        // If so, player can move forward, otherwise next position is an obstacle
        fun checkForObstacle(currentPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
            val currentFig = board[currentPos.first][currentPos.second]
            val nextFig = board[nextPos.first][nextPos.second]
            return (currentFig == 0 || currentFig.sign == color) &&
                    (nextFig == 0 || nextFig.sign == -color)
        }

        fun checkIfOnBoard(pos: Pair<Int, Int>): Boolean = (0..7).contains(pos.first) && (0..7).contains(pos.second)

        fun fKing(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            val rows = ((pos.first - 1)..(pos.first + 1)).toList().filter{(0..7).contains(it)}
            val cols = ((pos.second - 1)..(pos.second + 1)).toList().filter{(0..7).contains(it)}

            rows.forEach{row ->
                cols.forEach{col ->
                    if (board[row][col] == 0) positions += Pair(row, col)
                }}
            return positions
        }

        fun fRook(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()

            // Scan vertically and horizontally to detect obstacles
            // Need to check both sides of current pos:
            // towards 0 and towards 7
            arrayOf(0, 7).forEach{endPoint ->
                run {
                    val order = if (endPoint == 0) -1 else 1  // Use this val to be able iterate in ascending and descending orders
                    var row = pos.first // start position

                    // Iterating over constant column
                    // First check if we are still on board and then if no obstacles in the next position
                    while ((row * order < endPoint) &&
                        checkForObstacle(Pair(row, pos.second), Pair(row+order, pos.second))) {
                        row += order
                        positions += Pair(row, pos.second)
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
                    }
                }
            }
            return positions
        }

        fun fBishop(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()

            fun scanDiag1(): Unit {
                arrayOf(0, 7).forEach{endPoint ->
                    run {
                        val order = if (endPoint == 0) -1 else 1
                        var row = pos.first
                        var col = pos.second

                        // First check if we are still on board and then if no obstacles in the next position
                        while ((row * order < endPoint) &&
                            (col * order < endPoint) &&
                            checkForObstacle(Pair(row, col), Pair(row+order, col+order))) {
                            row += order
                            col += order
                            positions += Pair(row, col)
                        }
                    }
                }
            }

            fun scanDiag2(): Unit {
                arrayOf(Pair(0, 7), Pair(7, 0)).forEach{endPoints ->
                    run {
                        val order = if (endPoints.first == 0) -1 else 1
                        var row = pos.first
                        var col = pos.second

                        while ((row*order < endPoints.first) &&
                            (col*(-1)*order < endPoints.second) &&
                            checkForObstacle(Pair(row, col), Pair(row + order, col - order))) {
                            row += order
                            col -= order
                            positions += Pair(row, col)
                        }
                    }
                }
            }

            scanDiag1()
            scanDiag2()
            return positions
        }

        fun fQueen(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            // Queen moves just like Rook and Bishop together
            val diagonalPositions = fBishop(pos)
            val linesPositions = fRook(pos)

            return diagonalPositions.union(linesPositions).toMutableList()
        }

        fun fKnight(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            arrayOf(Pair(1,1), Pair(-1,-1), Pair(1, -1), Pair(-1,1)).forEach{signs ->
                run {
                    positions += Pair(pos.first + 2 * signs.first, pos.second + 1 * signs.second)
                    positions += Pair(pos.first + 1 * signs.first, pos.second + 2 * signs.second)
                }
            }

            fun checkIfObstacle(pos: Pair<Int, Int>): Boolean = (board[pos.first][pos.second]).sign != color

            return positions.filter{(checkIfOnBoard(it) && checkIfObstacle(it))}.toMutableList()
        }

        fun fPawn(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val movePositions = mutableListOf<Pair<Int, Int>>()
            val nextRow = pos.first + color

            // Helper function to determine if any attack moves are possible
            fun checkIfEnemy(pos: Pair<Int, Int>): Boolean = board[pos.first][pos.second].sign == -color

            // Custom function for pawn 'cause it has different logic of moving and capturing
            // It can't move to tne next position if it's occupied by an enemy
            fun checkPawnForObstacle(currentPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
                val currentFig = board[currentPos.first][currentPos.second]
                val nextFig = board[nextPos.first][nextPos.second]
                return (currentFig == 0 || currentFig.sign == color) && (nextFig == 0)
            }

            // If haven't done any move is possible to move in 2 positions forward
            if (pos.first == initialRowPos + color) {
                var row = pos.first
                while (checkPawnForObstacle(Pair(row, pos.second), Pair(row+color, pos.second)) &&
                    row != initialRowPos + 3*color) {
                    row += color
                    movePositions += Pair(row, pos.second)

                }
            }
            else if ((0..7).contains(nextRow) && board[nextRow][pos.second] == 0) movePositions += Pair(nextRow, pos.second)

            val attackPositions = mutableListOf(Pair(nextRow, pos.second - 1), Pair(nextRow, pos.second + 1)).filter {
                    move -> (checkIfOnBoard(move) && checkIfEnemy(move))
            }

            return movePositions.union(attackPositions).toMutableList()
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

        // update of available moves itself
        for ((pieceNum, piece) in pieces) {

            val pieceName = piece.first
            val piecePos = piece.second
            availableMoves[pieceNum] = applyFunction(pieceName, piecePos)
        }
    }
}