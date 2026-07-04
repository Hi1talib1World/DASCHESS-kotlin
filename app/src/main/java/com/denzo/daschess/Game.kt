package com.denzo.daschess

import kotlin.math.abs

class Game {
    val gameUtils = GameUtils()
    private val chessObjects = gameUtils.initGame()
    val capturedPiecesQueue: capturedQueue = mutableListOf()

    var playerBlack = chessObjects.first
    var playerWhite = chessObjects.second
    var board = chessObjects.third

    var players: Map<Int, Player> = mapOf(-1 to playerWhite, 1 to playerBlack)

    var isEnd = 0
    val isCheck = mutableMapOf<Int, Boolean>(-1 to false, 1 to false)
    var currentPlayerColor = -1
    var isAiEnabled = false

    var lastMoveCurrentPos: Pair<Int, Int>? = null
    var lastMovePreviousPos: Pair<Int, Int>? = null
    var lastMovedPieceNum: Int = 0

    init {
        gameUtils.updateAllAvailableMoves(players, board, lastMoveCurrentPos, lastMovePreviousPos, lastMovedPieceNum)
    }

    fun copy(): Game {
        val newGame = Game()
        newGame.currentPlayerColor = currentPlayerColor
        newGame.isAiEnabled = isAiEnabled
        newGame.isEnd = isEnd
        newGame.lastMoveCurrentPos = lastMoveCurrentPos
        newGame.lastMovePreviousPos = lastMovePreviousPos
        newGame.lastMovedPieceNum = lastMovedPieceNum
        
        newGame.isCheck.clear()
        newGame.isCheck.putAll(isCheck)
        
        newGame.capturedPiecesQueue.clear()
        newGame.capturedPiecesQueue.addAll(capturedPiecesQueue)
        
        newGame.playerWhite = playerWhite.copy()
        newGame.playerBlack = playerBlack.copy()
        newGame.players = mapOf(-1 to newGame.playerWhite, 1 to newGame.playerBlack)
        
        for (i in 0..7) {
            for (j in 0..7) {
                newGame.board[i][j] = board[i][j]
            }
        }
        return newGame
    }

    fun cancelMove() {
        if (lastMovePreviousPos != null && lastMoveCurrentPos != null) {
            currentPlayerColor *= -1
            gameUtils.cancelMove(players, currentPlayerColor, board, lastMoveCurrentPos!!, lastMovePreviousPos!!, capturedPiecesQueue)
            gameUtils.updateAllAvailableMoves(players, board, lastMoveCurrentPos, lastMovePreviousPos, lastMovedPieceNum)
            isEnd = gameUtils.checkEnd(players, board, capturedPiecesQueue)
            lastMoveCurrentPos = null
            lastMovePreviousPos = null
            lastMovedPieceNum = 0
        }
    }

    fun makeMove(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>, promotionChoice: String = "Queen") {
        val player = players[currentPlayerColor]!!
        val opponent = players[-1 * currentPlayerColor]!!
        val pieceNum = board[piecePos.first][piecePos.second]
        val pieceName = player.pieces[pieceNum]?.first

        if (pieceName == "King" && abs(piecePos.second - movePos.second) == 2) {
            if (gameUtils.isCheck(piecePos, opponent, board)) return
            val passedCol = if (movePos.second == 6) 5 else 3
            if (gameUtils.isCheck(Pair(piecePos.first, passedCol), opponent, board)) return
        }

        gameUtils.makeMove(players, currentPlayerColor, board, piecePos, movePos, capturedPiecesQueue, promotionChoice)
        
        lastMovedPieceNum = pieceNum
        lastMovePreviousPos = piecePos
        lastMoveCurrentPos = movePos

        gameUtils.updateAllAvailableMoves(players, board, lastMoveCurrentPos, lastMovePreviousPos, lastMovedPieceNum)

        isCheck[currentPlayerColor] = gameUtils.isCheck(players[currentPlayerColor]!!.pieces[currentPlayerColor]!!.second, players[-1*currentPlayerColor] as Player, board)
        isCheck[-1*currentPlayerColor] = gameUtils.isCheck(players[-1*currentPlayerColor]!!.pieces[-1*currentPlayerColor]!!.second, players[currentPlayerColor] as Player, board)

        if (isCheck[currentPlayerColor] == true) {
            gameUtils.cancelMove(players, currentPlayerColor, board, movePos, piecePos, capturedPiecesQueue)
            gameUtils.updateAllAvailableMoves(players, board, lastMoveCurrentPos, lastMovePreviousPos, lastMovedPieceNum)
        } else {
            lastMoveCurrentPos = movePos
            lastMovePreviousPos = piecePos
            currentPlayerColor *= -1
            isEnd = gameUtils.checkEnd(players, board, capturedPiecesQueue)
        }
    }
}