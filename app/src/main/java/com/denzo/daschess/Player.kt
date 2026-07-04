package com.denzo.daschess

import kotlin.math.abs
import kotlin.math.sign

class Player(var color: Int) {
    // -1 = white, 1 = black
    private val initialRowPos = if (color == 1) 0 else 7

    // TODO: make Piece object and replace all pair in maps to them

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

        // Check fig positions for several cases:
        //
        // First: current position is occupied by player's side, next by other side or empty
        // Second: current position is empty and next is occupied by other side or empty
        //
        // If so, player can move forward, otherwise next position is an obstacle
        fun checkForObstacle(currentPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
            val currentFig = board[currentPos.first][currentPos.second]
            val nextFig = board[nextPos.first][nextPos.second]
            
            val currentSign = if (currentFig > 0) 1 else if (currentFig < 0) -1 else 0
            val nextSign = if (nextFig > 0) 1 else if (nextFig < 0) -1 else 0
            
            return (currentFig == 0 || currentSign == color) &&
                    (nextFig == 0 || nextSign == -color)
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
}

        fun checkIfOnBoard(pos: Pair<Int, Int>): Boolean = (0..7).contains(pos.first) && (0..7).contains(pos.second)

        fun fKing(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            val rows = ((pos.first - 1)..(pos.first + 1)).toList().filter{(checkIfOnBoard(Pair(it, pos.second)))    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.pieces.clear()
        newPlayer.pieces.putAll(pieces)
        newPlayer.availableMoves.clear()
        newPlayer.availableMoves.putAll(availableMoves)
        newPlayer.pieceMoveCounts.clear()
        newPlayer.pieceMoveCounts.putAll(pieceMoveCounts)
        return newPlayer
    }
}
            val cols = ((pos.second - 1)..(pos.second + 1)).toList().filter{(checkIfOnBoard(Pair(pos.first, it)))    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.pieces.clear()
        newPlayer.pieces.putAll(pieces)
        newPlayer.availableMoves.clear()
        newPlayer.availableMoves.putAll(availableMoves)
        newPlayer.pieceMoveCounts.clear()
        newPlayer.pieceMoveCounts.putAll(pieceMoveCounts)
        return newPlayer
    }
}

            rows.forEach{row ->
                cols.forEach{col ->
                    if (Pair(row, col) != pos) {
                        val fig = board[row][col]
                        val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                        if (fig == 0 || figSign == -color) {
                            positions += Pair(row, col)
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
}
                }    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.pieces.clear()
        newPlayer.pieces.putAll(pieces)
        newPlayer.availableMoves.clear()
        newPlayer.availableMoves.putAll(availableMoves)
        newPlayer.pieceMoveCounts.clear()
        newPlayer.pieceMoveCounts.putAll(pieceMoveCounts)
        return newPlayer
    }
}

            // Castling logic (Simplified: checks path and movement, "under attack" checked in makeMove)
            val kingNum = 1 * color
            if ((pieceMoveCounts[kingNum] ?: 0) == 0) {
                // Kingside Castling (towards Rook at col 7)
                val rookKingsideNum = 4 * color
                if ((pieceMoveCounts[rookKingsideNum] ?: 0) == 0) {
                    if (board[initialRowPos][5] == 0 && board[initialRowPos][6] == 0) {
                        positions += Pair(initialRowPos, 6) // King moves 2 squares to g-file
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
}
                // Queenside Castling (towards Rook at col 0)
                val rookQueensideNum = 3 * color
                if ((pieceMoveCounts[rookQueensideNum] ?: 0) == 0) {
                    if (board[initialRowPos][1] == 0 && board[initialRowPos][2] == 0 && board[initialRowPos][3] == 0) {
                        positions += Pair(initialRowPos, 2) // King moves 2 squares to c-file
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
}
            return positions
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
}
                }    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.pieces.clear()
        newPlayer.pieces.putAll(pieces)
        newPlayer.availableMoves.clear()
        newPlayer.availableMoves.putAll(availableMoves)
        newPlayer.pieceMoveCounts.clear()
        newPlayer.pieceMoveCounts.putAll(pieceMoveCounts)
        return newPlayer
    }
}

            arrayOf(0, 7).forEach { endPoint ->
                run {
                    val order = if (endPoint == 0) -1 else 1
                    var col = pos.second

                    while ((col * order < endPoint) &&
                        checkForObstacle(Pair(pos.first, col), Pair(pos.first, col+order))){
                        col += order
                        positions += Pair(pos.first, col)
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
}
            return positions
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
}

            scanDiag1()
            scanDiag2()
            return positions
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
}

        fun fQueen(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            // Queen moves just like Rook and Bishop together
            val diagonalPositions = fBishop(pos)
            val linesPositions = fRook(pos)

            return diagonalPositions.union(linesPositions).toMutableList()
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
}

        fun fKnight(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val positions = mutableListOf<Pair<Int, Int>>()
            arrayOf(Pair(1,1), Pair(-1,-1), Pair(1, -1), Pair(-1,1)).forEach{signs ->
                run {
                    positions += Pair(pos.first + 2 * signs.first, pos.second + 1 * signs.second)
                    positions += Pair(pos.first + 1 * signs.first, pos.second + 2 * signs.second)
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
}

            fun checkIfObstacle(pos: Pair<Int, Int>): Boolean {
                val fig = board[pos.first][pos.second]
                val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                return figSign != color
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
}

            return positions.filter{(checkIfOnBoard(it) && checkIfObstacle(it))}.toMutableList()
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
}

        fun fPawn(pos: Pair<Int, Int>): MutableList<Pair<Int, Int>> {
            val movePositions = mutableListOf<Pair<Int, Int>>()
            val nextRow = pos.first + color

            // Helper function to determine if any attack moves are possible
            fun checkIfEnemy(pos: Pair<Int, Int>): Boolean {
                val fig = board[pos.first][pos.second]
                val figSign = if (fig > 0) 1 else if (fig < 0) -1 else 0
                return figSign == -color
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
}

            // Custom function for pawn 'cause it has different logic of moving and capturing
            // It can't move to tne next position if it's occupied by an enemy
            fun checkPawnForObstacle(currentPos: Pair<Int, Int>, nextPos: Pair<Int, Int>): Boolean {
                val currentFig = board[currentPos.first][currentPos.second]
                val nextFig = board[nextPos.first][nextPos.second]
                return (currentFig == 0 || currentFig.sign == color) && (nextFig == 0)
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
}

            // If haven't done any move is possible to move in 2 positions forward
            if (pos.first == initialRowPos + color) {
                var row = pos.first
                while (checkPawnForObstacle(Pair(row, pos.second), Pair(row+color, pos.second)) &&
                    row != initialRowPos + 3*color) {
                    row += color
                    movePositions += Pair(row, pos.second)

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
}
            else if ((0..7).contains(nextRow) && board[nextRow][pos.second] == 0) movePositions += Pair(nextRow, pos.second)

            val attackPositions = mutableListOf(Pair(nextRow, pos.second - 1), Pair(nextRow, pos.second + 1)).filter {
                    move -> (checkIfOnBoard(move) && checkIfEnemy(move))
            }.toMutableList()

            // En Passant detection
            if (lastMoveCurrent != null && lastMovePrevious != null) {
                // If last move was an enemy pawn moving 2 squares
                val lastPieceSign = if (lastMovedPiece > 0) 1 else if (lastMovedPiece < 0) -1 else 0
                if (lastPieceSign == -color && abs(lastMoveCurrent.first - lastMovePrevious.first) == 2) {
                    // Check if it's currently adjacent to our pawn
                    if (lastMoveCurrent.first == pos.first && abs(lastMoveCurrent.second - pos.second) == 1) {
                        // Square to move into is behind the enemy pawn
                        attackPositions += Pair(pos.first + color, lastMoveCurrent.second)
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
}

            return movePositions.union(attackPositions).toMutableList()
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
}

        // update of available moves itself
        for ((pieceNum, piece) in pieces) {

            val pieceName = piece.first
            val piecePos = piece.second
            availableMoves[pieceNum] = applyFunction(pieceName, piecePos)
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
}
}