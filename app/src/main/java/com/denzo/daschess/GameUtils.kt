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

    fun updateAllAvailableMoves(players: Map<Int, Player>, board: Array<IntArray>): Unit {
        for (player in players.values) player.updateAvailableMoves(board)
    }

    fun getAvailableMovesForPiece(pieceNum: Int, currentPlayer: Player?): List<Pair<Int, Int>> {
        return currentPlayer!!.availableMoves[pieceNum]!!
    }

    fun makeMove(
        players: Map<Int, Player>,
        currentPlayer: Int,
        board: Array<IntArray>,
        currentPos: Pair<Int, Int>,
        movePos: Pair<Int, Int>,
        capturedPiecesQueue: capturedQueue
    ): Unit {
        val player = players[currentPlayer]!!
        val otherPlayer = players[currentPlayer * -1]!!

        val pieceNum = board[currentPos.first][currentPos.second] // number of chosen piece
        val pieceName = player.pieces[pieceNum]!!.first

        // Detect and handle castling
        if (pieceName == "King" && abs(currentPos.second - movePos.second) == 2) {
            val isKingside = movePos.second == 5
            val rookCol = if (isKingside) 7 else 0
            val rookNewCol = if (isKingside) 4 else 2
            val rookNum = board[currentPos.first][rookCol]
            val rookName = player.pieces[rookNum]!!.first

            // Move Rook on board
            board[currentPos.first][rookNewCol] = rookNum
            board[currentPos.first][rookCol] = 0

            // Update Rook in player's pieces
            player.pieces[rookNum] = Pair(rookName, Pair(currentPos.first, rookNewCol))
            player.movedPieces.add(rookNum)
        }

        // Standard move: check if position occupied by piece of other player -> capture it
        val pieceOnMovePosition = board[movePos.first][movePos.second]
        if (pieceOnMovePosition != 0) {
            val capturedPieceInfo = otherPlayer.pieces[pieceOnMovePosition]
            capturedPiecesQueue.add(Triple(pieceOnMovePosition, capturedPieceInfo!!.first, capturedPieceInfo.second))
            otherPlayer.pieces.remove(pieceOnMovePosition)
        }

        // Move current player's piece on the board
        board[movePos.first][movePos.second] = pieceNum
        board[currentPos.first][currentPos.second] = 0

        // Update position info of piece in player's map
        player.pieces[pieceNum] = Pair(pieceName, movePos)
        player.movedPieces.add(pieceNum)

        // Pawn Promotion
        if (pieceName == "Pawn" && (movePos.first == 0 || movePos.first == 7)) {
            promotePawn(player, board, movePos, pieceNum)
        }
    }

    private fun promotePawn(player: Player, board: Array<IntArray>, pos: Pair<Int, Int>, pawnNum: Int) {
        // Automatically promote to Queen for now
        val promotedPieceName = "Queen"
        // Update the piece map with the new name but keep the same piece number
        player.pieces[pawnNum] = Pair(promotedPieceName, pos)
    }

    fun cancelMove(
        players: Map<Int, Player>,
        currentPlayer: Int,
        board: Array<IntArray>,
        currentPos: Pair<Int, Int>,
        previousPos: Pair<Int, Int>,
        capturedPiecesQueue: capturedQueue
    ): Unit {
        val player = players[currentPlayer]!!
        val pieceNum = board[currentPos.first][currentPos.second]
        val pieceName = player.pieces[pieceNum]!!.first

        // Detect and revert castling
        if (pieceName == "King" && abs(currentPos.second - previousPos.second) == 2) {
            val isKingside = currentPos.second == 5
            val rookCol = if (isKingside) 7 else 0
            val rookNewCol = if (isKingside) 4 else 2
            val rookNum = board[currentPos.first][rookNewCol]
            val rookName = player.pieces[rookNum]!!.first

            // Move Rook back on board
            board[currentPos.first][rookCol] = rookNum
            board[currentPos.first][rookNewCol] = 0

            // Update Rook in player's pieces
            player.pieces[rookNum] = Pair(rookName, Pair(currentPos.first, rookCol))
            player.movedPieces.remove(rookNum)
        }

        // Return current piece to previous position
        board[previousPos.first][previousPos.second] = pieceNum
        board[currentPos.first][currentPos.second] =
            if (capturedPiecesQueue.isNotEmpty() && capturedPiecesQueue.last().third == currentPos) {
                // return captured piece to Player's object
                val capturedPiece = capturedPiecesQueue.last()
                players[-1*currentPlayer]?.pieces?.set(capturedPiece.first,
                    Pair(capturedPiece.second, capturedPiece.third)
                )
                capturedPiecesQueue.removeAt(capturedPiecesQueue.lastIndex)

                capturedPiece.first
            }
            else 0

        // Update piece position in Player's object
        player.pieces[pieceNum] = Pair(pieceName, previousPos)

        // Revert Promotion if necessary
        if (pieceName == "Queen" && (previousPos.first == 1 || previousPos.first == 6) && (currentPos.first == 0 || currentPos.first == 7)) {
            // This is a heuristic: if a Queen moved back from a promotion square to a start-pawn square, 
            // and it was originally a pawn, we change it back. 
            // In a more robust engine, we'd store the original piece type in a move history.
            player.pieces[pieceNum] = Pair("Pawn", previousPos)
        }

        // Note: Reverting movedPieces for the main piece is complex because we don't know if it moved before.
        // For King/Rook, we might accidentally allow castling again if we remove it, but it's rare to cancel 
        // a non-first move and expect castling to be valid. 
        // A better way would be a move history log.
        player.movedPieces.remove(pieceNum) 
    }

    fun isCheck(kingPos: Pair<Int, Int>, attacker: Player): Boolean {
        val attackerPossibleMoves = attacker.availableMoves
        return (attackerPossibleMoves.values.any { list -> list.contains(kingPos) })
    }

    fun isCheckmate(defender: Player, attacker: Player): Boolean {
        val allPossibleKingMoves = defender.availableMoves[defender.color] ?: emptyList()
        val currentKingPos = defender.pieces[defender.color]!!.second
        return (allPossibleKingMoves + currentKingPos).all { pos -> isCheck(pos, attacker)
        }
    }

    fun checkEnd(players: Map<Int, Player>): Int {
        // return a color of a winner if checkmate or 0 otherwise
        return when {
            isCheckmate(players[1] as Player, players[-1] as Player) -> -1
            isCheckmate(players[-1] as Player, players[1] as Player) -> 1
            else -> 0
        }
    }

    fun initGame(): Triple<Player, Player, Array<IntArray>> {
        val playerBlack = Player(1)
        val playerWhite = Player(-1)
        val board = initBoard(arrayOf(playerWhite, playerBlack))

        return Triple(playerBlack, playerWhite, board)
    }
}