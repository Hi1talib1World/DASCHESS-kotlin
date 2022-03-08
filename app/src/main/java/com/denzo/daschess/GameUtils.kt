package com.denzo.daschess

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
        val otherPlayer = players[currentPlayer * -1] as Player

        val pieceNum = board[currentPos.first][currentPos.second] // number of chosen piece
        val pieceName = players[currentPlayer]?.pieces?.get(pieceNum)!!.first

        // Make move
        val pieceOnMovePosition = board[movePos.first][movePos.second]
        // If position occupied by piece of other player -> capture it
        if (pieceOnMovePosition != 0) {
            val capturedPieceInfo = otherPlayer.pieces[pieceOnMovePosition]
            capturedPiecesQueue.add(Triple(pieceOnMovePosition, capturedPieceInfo!!.first, capturedPieceInfo.second))
            otherPlayer.pieces.remove(pieceOnMovePosition)
        }

        // Move current player's piece on the board
        board[movePos.first][movePos.second] = pieceNum
        board[currentPos.first][currentPos.second] = 0

        // Update position info of piece in player's map
        players[currentPlayer]?.pieces!![pieceNum] = Pair(pieceName, movePos)
    }

    fun cancelMove(
        players: Map<Int, Player>,
        currentPlayer: Int,
        board: Array<IntArray>,
        currentPos: Pair<Int, Int>,
        previousPos: Pair<Int, Int>,
        capturedPiecesQueue: capturedQueue
    ): Unit {
        val pieceNum = board[currentPos.first][currentPos.second]
        println("piece num to cancel: $pieceNum")
        val pieceName = players[currentPlayer]?.pieces?.get(pieceNum)!!.first

        // Return captured piece on board or just remove current piece from this position
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
        players[currentPlayer]?.pieces!![pieceNum] = Pair(pieceName, previousPos)
    }

    fun isCheck(kingPos: Pair<Int, Int>, attacker: Player): Boolean {
        val attackerPossibleMoves = attacker.availableMoves
        return (attackerPossibleMoves.values.any { list -> list.contains(kingPos) })
    }

    fun isCheckmate(defender: Player, attacker: Player): Boolean {
        val allPossibleKingMoves = defender.availableMoves[defender.color]
        val currentKingPos = defender.pieces[defender.color]!!.second
        return (allPossibleKingMoves!! + currentKingPos).all { pos -> isCheck(pos, attacker)
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
        // Queue of captured pieces to implement a move cancellation

        return Triple(playerBlack, playerWhite, board)
    }
}