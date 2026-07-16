package com.denzo.daschess

import kotlin.math.abs

class GameUtils {

    /*
    * Helper class containing methods for initialization of game objects, updating their sates and
    * checking a state of the game
    */

    // Board represented as a 2d array, white figs down, black up.
    // -1 = white, 1 = black
    private fun initBoard(players: Array<Player>): Array<IntArray> {
        val board = Array(8) { IntArray(8) }
        for (player in players) {

            player.pieces.forEach { (pieceNum, piece) ->
                run {
                    val pos = piece.second
                    board[pos.first][pos.second] = pieceNum
                }
            }
        }
        return board
    }

    fun updateAllAvailableMoves(
        players: Map<Int, Player>,
        board: Array<IntArray>,
        lastMoveCurrent: Pair<Int, Int>? = null,
        lastMovePrevious: Pair<Int, Int>? = null,
        lastMovedPiece: Int = 0
    ): Unit {
        for (player in players.values) player.updateAvailableMoves(board, lastMoveCurrent, lastMovePrevious, lastMovedPiece)
    }

    fun getAvailableMovesForPiece(pieceNum: Int, currentPlayer: Player?): List<Pair<Int, Int>> {
        return currentPlayer!!.availableMoves[pieceNum] ?: emptyList()
    }

    fun makeMove(
        players: Map<Int, Player>,
        currentPlayer: Int,
        board: Array<IntArray>,
        currentPos: Pair<Int, Int>,
        movePos: Pair<Int, Int>,
        capturedPiecesQueue: capturedQueue,
        promotionChoice: String = "Queen"
    ): Unit {
        val player = players[currentPlayer] ?: return
        val otherPlayer = players[currentPlayer * -1] ?: return

        val pieceNum = board[currentPos.first][currentPos.second]
        if (pieceNum == 0) return
        
        val pieceName = player.pieces[pieceNum]?.first ?: return

        // Detect and handle castling
        if (pieceName == "King" && abs(currentPos.second - movePos.second) == 2) {
            val isKingside = movePos.second == 6
            val rookCol = if (isKingside) 7 else 0
            val rookNewCol = if (isKingside) 5 else 3
            val rookNum = board[currentPos.first][rookCol]
            
            if (rookNum != 0) {
                val rookName = player.pieces[rookNum]?.first ?: "Rook"
                // Move Rook on board
                board[currentPos.first][rookNewCol] = rookNum
                board[currentPos.first][rookCol] = 0

                // Update Rook in player's pieces
                player.pieces[rookNum] = Pair(rookName, Pair(currentPos.first, rookNewCol))
                player.pieceMoveCounts[rookNum] = (player.pieceMoveCounts[rookNum] ?: 0) + 1
            }
        }

        // Standard move: check if position occupied by piece of other player -> capture it
        val pieceOnMovePosition = board[movePos.first][movePos.second]
        if (pieceOnMovePosition != 0) {
            val capturedPieceInfo = otherPlayer.pieces[pieceOnMovePosition]
            if (capturedPieceInfo != null) {
                capturedPiecesQueue.add(Triple(pieceOnMovePosition, capturedPieceInfo.first, capturedPieceInfo.second))
                otherPlayer.pieces.remove(pieceOnMovePosition)
            } else {
                // Defensive check: if piece exists on board but not in map (shouldn't happen)
            }
        } else {
            // Check for potential ghost moves or special captures
        }

        // Move current player's piece on the board
        board[movePos.first][movePos.second] = pieceNum
        board[currentPos.first][currentPos.second] = 0

        // Update position info of piece in player's map
        player.pieces[pieceNum] = Pair(pieceName, movePos)
        player.pieceMoveCounts[pieceNum] = (player.pieceMoveCounts[pieceNum] ?: 0) + 1

        // Pawn Promotion
        if (pieceName == "Pawn" && (movePos.first == 0 || movePos.first == 7)) {
            promotePawn(player, board, movePos, pieceNum, promotionChoice)
        }

        // En Passant Capture Logic
        if (pieceName == "Pawn" && currentPos.second != movePos.second && pieceOnMovePosition == 0) {
            // This is an En Passant move (diagonal move to empty square)
            val capturedPawnPos = Pair(currentPos.first, movePos.second)
            val capturedPawnNum = board[capturedPawnPos.first][capturedPawnPos.second]
            
            if (capturedPawnNum != 0) {
                val capturedPawnName = otherPlayer.pieces[capturedPawnNum]?.first ?: "Pawn"

                // Record capture
                capturedPiecesQueue.add(Triple(capturedPawnNum, capturedPawnName, capturedPawnPos))
                
                // Remove from board and other player
                board[capturedPawnPos.first][capturedPawnPos.second] = 0
                otherPlayer.pieces.remove(capturedPawnNum)
            }
        }
    }

    private fun promotePawn(player: Player, board: Array<IntArray>, pos: Pair<Int, Int>, pawnNum: Int, choice: String) {
        player.pieces[pawnNum] = Pair(choice, pos)
    }

    fun cancelMove(
        players: Map<Int, Player>,
        currentPlayer: Int,
        board: Array<IntArray>,
        currentPos: Pair<Int, Int>,
        previousPos: Pair<Int, Int>,
        capturedPiecesQueue: capturedQueue
    ): Unit {
        val player = players[currentPlayer] ?: return
        val pieceNum = board[currentPos.first][currentPos.second]
        if (pieceNum == 0) return
        
        val pieceName = player.pieces[pieceNum]?.first ?: return

        // Detect and revert castling
        if (pieceName == "King" && abs(currentPos.second - previousPos.second) == 2) {
            val isKingside = currentPos.second == 6
            val rookCol = if (isKingside) 7 else 0
            val rookNewCol = if (isKingside) 5 else 3
            val rookNum = board[currentPos.first][rookNewCol]
            
            if (rookNum != 0) {
                val rookName = player.pieces[rookNum]?.first ?: "Rook"
                // Move Rook back on board
                board[currentPos.first][rookCol] = rookNum
                board[currentPos.first][rookNewCol] = 0

                // Update Rook in player's pieces
                player.pieces[rookNum] = Pair(rookName, Pair(currentPos.first, rookCol))
                player.pieceMoveCounts[rookNum] = (player.pieceMoveCounts[rookNum] ?: 1) - 1
            }
        }

        // Return current piece to previous position
        board[previousPos.first][previousPos.second] = pieceNum
        
        // Handle restoration of captured piece
        if (capturedPiecesQueue.isNotEmpty()) {
            val lastCaptured = capturedPiecesQueue.last()
            
            // Heuristic for matching capture to current move
            val isStandardCapture = lastCaptured.third == currentPos
            val isEnPassantCapture = (pieceName == "Pawn" && lastCaptured.third.first == previousPos.first && lastCaptured.third.second == currentPos.second)
            
            if (isStandardCapture || isEnPassantCapture) {
                val capturedPiece = capturedPiecesQueue.removeAt(capturedPiecesQueue.lastIndex)
                val otherPlayer = players[-1 * currentPlayer]!!
                otherPlayer.pieces[capturedPiece.first] = Pair(capturedPiece.second, capturedPiece.third)
                board[capturedPiece.third.first][capturedPiece.third.second] = capturedPiece.first

                if (!isStandardCapture) {
                    board[currentPos.first][currentPos.second] = 0
                }
            } else {
                board[currentPos.first][currentPos.second] = 0
            }
        } else {
            board[currentPos.first][currentPos.second] = 0
        }

        // Update piece position in Player's object
        player.pieces[pieceNum] = Pair(pieceName, previousPos)
        player.pieceMoveCounts[pieceNum] = (player.pieceMoveCounts[pieceNum] ?: 1) - 1

        // Revert Promotion if necessary
        if (pieceName != "Pawn" && (previousPos.first == 1 || previousPos.first == 6) && (currentPos.first == 0 || currentPos.first == 7)) {
            player.pieces[pieceNum] = Pair("Pawn", previousPos)
        }
    }

    fun isCheck(kingPos: Pair<Int, Int>, attacker: Player, board: Array<IntArray>): Boolean {
        for ((_, pieceInfo) in attacker.pieces) {
            val pieceName = pieceInfo.first
            val pos = pieceInfo.second
            
            val canAttack = when (pieceName) {
                "Pawn" -> {
                    val direction = -attacker.color
                    kingPos.first == pos.first + direction && abs(kingPos.second - pos.second) == 1
                }
                "Knight" -> {
                    val dr = abs(kingPos.first - pos.first)
                    val dc = abs(kingPos.second - pos.second)
                    (dr == 2 && dc == 1) || (dr == 1 && dc == 2)
                }
                "King" -> {
                    abs(kingPos.first - pos.first) <= 1 && abs(kingPos.second - pos.second) <= 1
                }
                "Rook" -> canReachStraight(pos, kingPos, board)
                "Bishop" -> canReachDiagonal(pos, kingPos, board)
                "Queen" -> canReachStraight(pos, kingPos, board) || canReachDiagonal(pos, kingPos, board)
                else -> false
            }
            if (canAttack) return true
        }
        return false
    }

    private fun canReachStraight(from: Pair<Int, Int>, to: Pair<Int, Int>, board: Array<IntArray>): Boolean {
        if (from.first != to.first && from.second != to.second) return false
        val dr = if (to.first > from.first) 1 else if (to.first < from.first) -1 else 0
        val dc = if (to.second > from.second) 1 else if (to.second < from.second) -1 else 0
        var r = from.first + dr
        var c = from.second + dc
        while (r != to.first || c != to.second) {
            if (board[r][c] != 0) return false
            r += dr
            c += dc
        }
        return true
    }

    private fun canReachDiagonal(from: Pair<Int, Int>, to: Pair<Int, Int>, board: Array<IntArray>): Boolean {
        if (abs(from.first - to.first) != abs(from.second - to.second)) return false
        val dr = if (to.first > from.first) 1 else if (to.first < from.first) -1 else 0
        val dc = if (to.second > from.second) 1 else if (to.second < from.second) -1 else 0
        var r = from.first + dr
        var c = from.second + dc
        while (r != to.first || c != to.second) {
            if (board[r][c] != 0) return false
            r += dr
            c += dc
        }
        return true
    }

    fun hasAnyLegalMove(
        players: Map<Int, Player>,
        color: Int,
        board: Array<IntArray>,
        capturedPiecesQueue: capturedQueue
    ): Boolean {
        val player = players[color] ?: return false
        val opponent = players[-1 * color] ?: return false
        val movesToTry = player.availableMoves.map { it.key to it.value }
        
        for ((pieceNum, moves) in movesToTry) {
            val currentPos = player.pieces[pieceNum]?.second ?: continue
            for (movePos in moves) {
                makeMove(players, color, board, currentPos, movePos, capturedPiecesQueue)
                val kingPiece = player.pieces[color] ?: continue
                val stillInCheck = isCheck(kingPiece.second, opponent, board)
                cancelMove(players, color, board, movePos, currentPos, capturedPiecesQueue)
                if (!stillInCheck) return true 
            }
        }
        return false
    }

    fun checkEnd(
        players: Map<Int, Player>,
        board: Array<IntArray>,
        capturedPiecesQueue: capturedQueue
    ): Int {
        fun getStatus(color: Int): Int {
            val opponentColor = -1 * color
            val player = players[color] ?: return 0
            val opponent = players[opponentColor] ?: return 0
            
            val kingPiece = player.pieces[color] ?: return 0
            val inCheck = isCheck(kingPiece.second, opponent, board)
            val hasMoves = hasAnyLegalMove(players, color, board, capturedPiecesQueue)
            
            return when {
                inCheck && !hasMoves -> opponentColor
                !inCheck && !hasMoves -> 2
                else -> 0
            }
        }

        val whiteStatus = getStatus(-1)
        if (whiteStatus != 0) return whiteStatus
        
        val blackStatus = getStatus(1)
        if (blackStatus != 0) return blackStatus

        if (isInsufficientMaterial(players)) return 2
        
        return 0
    }

    private fun isInsufficientMaterial(players: Map<Int, Player>): Boolean {
        val whitePieces = players[-1]?.pieces?.values?.map { it.first } ?: emptyList()
        val blackPieces = players[1]?.pieces?.values?.map { it.first } ?: emptyList()

        // If either side has a Pawn, Rook, or Queen, it's not insufficient material
        if (whitePieces.contains("Pawn") || blackPieces.contains("Pawn")) return false
        if (whitePieces.contains("Rook") || blackPieces.contains("Rook")) return false
        if (whitePieces.contains("Queen") || blackPieces.contains("Queen")) return false

        val whiteMinorCount = whitePieces.count { it == "Knight" || it == "Bishop" }
        val blackMinorCount = blackPieces.count { it == "Knight" || it == "Bishop" }

        // King vs King
        if (whiteMinorCount == 0 && blackMinorCount == 0) return true

        // King + Minor vs King
        if (whiteMinorCount == 1 && blackMinorCount == 0) return true
        if (blackMinorCount == 1 && whiteMinorCount == 0) return true

        // King + Minor vs King + Minor
        if (whiteMinorCount == 1 && blackMinorCount == 1) {
            val whiteMinor = players[-1]?.pieces?.values?.find { it.first == "Bishop" || it.first == "Knight" }
            val blackMinor = players[1]?.pieces?.values?.find { it.first == "Bishop" || it.first == "Knight" }
            
            if (whiteMinor?.first == "Bishop" && blackMinor?.first == "Bishop") {
                // Same colored bishops draw
                val whiteSquareColor = (whiteMinor.second.first + whiteMinor.second.second) % 2
                val blackSquareColor = (blackMinor.second.first + blackMinor.second.second) % 2
                if (whiteSquareColor == blackSquareColor) return true
            } else if (whiteMinor?.first == "Knight" && blackMinor?.first == "Knight") {
                // Often a draw but not strictly insufficient by some rules, 
                // but we can include it for "more if-else" logic.
                return true
            }
        } else if (whiteMinorCount == 2 && blackMinorCount == 0) {
            val minors = players[-1]?.pieces?.values?.filter { it.first == "Knight" }
            if (minors?.size == 2) return true // Two knights vs King cannot force mate
        } else if (blackMinorCount == 2 && whiteMinorCount == 0) {
            val minors = players[1]?.pieces?.values?.filter { it.first == "Knight" }
            if (minors?.size == 2) return true
        }

        return false

        return false
    }

    fun initGame(): Triple<Player, Player, Array<IntArray>> {
        val playerBlack = Player(1)
        val playerWhite = Player(-1)
        val board = initBoard(arrayOf(playerWhite, playerBlack))

        return Triple(playerBlack, playerWhite, board)
    }
}