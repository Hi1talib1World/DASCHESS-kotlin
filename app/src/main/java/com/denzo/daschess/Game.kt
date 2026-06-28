package com.denzo.daschess

import kotlin.math.abs

class Game {
    /*
    * Class that implements the game itself, stores it's instances of game objects,
    * keeps track of current player and winning state
    */

    val gameUtils = GameUtils()
    private val chessObjects = gameUtils.initGame()
    val capturedPiecesQueue: capturedQueue = mutableListOf()

    val playerBlack = chessObjects.first
    val playerWhite = chessObjects.second
    val board = chessObjects.third

    val players: Map<Int, Player> = mapOf(-1 to playerWhite, 1 to playerBlack)

    var isEnd = 0
    val isCheck = mutableMapOf<Int, Boolean>(-1 to false, 1 to false)
    var currentPlayerColor = -1  // first turn is white's turn
    var isAiEnabled = false

    // Variables to store values of positions of the last move to implement Cancellation of that move
    private var lastMoveCurrentPos: Pair<Int, Int>? = null
    private var lastMovePreviousPos: Pair<Int, Int>? = null

    init {
        gameUtils.updateAllAvailableMoves(players, board)
    }

    fun cancelMove() {
        if (lastMovePreviousPos != null && lastMoveCurrentPos != null) {
            // Change player back
            currentPlayerColor *= -1
            gameUtils.cancelMove(players,
                currentPlayerColor,
                board,
                lastMoveCurrentPos!!,
                lastMovePreviousPos!!,
                capturedPiecesQueue)

            gameUtils.updateAllAvailableMoves(players, board)
            isEnd = gameUtils.checkEnd(players)
            // reset these variables to not be able cancel move if it's invalid
            lastMoveCurrentPos = null
            lastMovePreviousPos = null
        }
    }

    fun makeMove(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>) {
        val player = players[currentPlayerColor]!!
        val opponent = players[-1 * currentPlayerColor]!!
        val pieceNum = board[piecePos.first][piecePos.second]
        val pieceName = player.pieces[pieceNum]?.first

        // Basic Check: Cannot castle while in check
        if (pieceName == "King" && abs(piecePos.second - movePos.second) == 2) {
            if (gameUtils.isCheck(piecePos, opponent)) {
                return // Invalid move: cannot castle while in check
            }
            // Cannot castle through a square under attack
            val passedCol = if (movePos.second == 5) 4 else 2
            if (gameUtils.isCheck(Pair(piecePos.first, passedCol), opponent)) {
                return // Invalid move: cannot castle through attack
            }
        }

        gameUtils.makeMove(players, currentPlayerColor, board, piecePos, movePos, capturedPiecesQueue)
        gameUtils.updateAllAvailableMoves(players, board)

        // Check if check for both players
        isCheck[currentPlayerColor] = gameUtils.isCheck(players[currentPlayerColor]!!.pieces[currentPlayerColor]!!.second, players[-1*currentPlayerColor] as Player)
        isCheck[-1*currentPlayerColor] = gameUtils.isCheck(players[-1*currentPlayerColor]!!.pieces[-1*currentPlayerColor]!!.second, players[currentPlayerColor] as Player)

        // Check if player made invalid move and open his king for opponent's attack
        if (isCheck[currentPlayerColor] == true) {
            gameUtils.cancelMove(players, currentPlayerColor, board, movePos, piecePos, capturedPiecesQueue)
            gameUtils.updateAllAvailableMoves(players, board)
        }
        else {
            // And assign them only in case if move is valid
            lastMoveCurrentPos = movePos
            lastMovePreviousPos = piecePos
            // Change current player to opponent
            currentPlayerColor *= -1
            // Update winning state
            isEnd = gameUtils.checkEnd(players)
        }
    }
}