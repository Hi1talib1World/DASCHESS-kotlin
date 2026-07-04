package com.denzo.daschess

import android.os.Handler
import android.os.Looper
import kotlin.concurrent.thread

class Presenter(private val view: ChessboardInterface) {

    private var game = Game()
    private val chessAI = ChessAI()

    private var isAiThinking = false
    private var lastAvailableMoves: List<Pair<Int, Int>> = listOf()
    private val moveHistory = mutableListOf<String>()

    fun cancelMove() {
        if (isAiThinking) return
        game.cancelMove()
        view.setLastMove(game.lastMovePreviousPos, game.lastMoveCurrentPos)
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces, game.currentPlayerColor)
    }

    fun restartGame(isAiEnabled: Boolean = false) {
        if (isAiThinking) return
        game = Game()
        game.isAiEnabled = isAiEnabled
        view.setLastMove(null, null)
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces, game.currentPlayerColor)
    }

    fun handleInput(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?): Boolean {
        if (isAiThinking) return false

        var lastSelection = 0
        if (previousPosition != null) {
            lastSelection = game.board[previousPosition.first][previousPosition.second]
        }

        val pieceNum = game.board[currentPosition!!.first][currentPosition.second]
        val currentPlayerNum = game.currentPlayerColor

        val pieceSign = if (pieceNum > 0) 1 else if (pieceNum < 0) -1 else 0
        val lastSelectionSign = if (lastSelection > 0) 1 else if (lastSelection < 0) -1 else 0

        return when {
            (pieceSign == currentPlayerNum) -> {
                selectPieceToMove(pieceNum, currentPlayerNum)
                true
            }
            (lastAvailableMoves.contains(currentPosition)
                    && lastSelectionSign == currentPlayerNum) -> {
                attemptMove(previousPosition!!, currentPosition)
                false // Finished move, reset selection
            }
            else -> {
                view.clearSelection()
                false
            }
        }
    }

    private fun selectPieceToMove(pieceNum: Int, currentPlayerNum: Int) {
        val pseudoMoves = game.gameUtils.getAvailableMovesForPiece(pieceNum, game.players[currentPlayerNum])
        
        val legalMoves = pseudoMoves.filter { movePos ->
            val player = game.players[currentPlayerNum]!!
            val currentPos = player.pieces[pieceNum]!!.second
            game.gameUtils.makeMove(game.players, currentPlayerNum, game.board, currentPos, movePos, game.capturedPiecesQueue)
            val kingPiece = player.pieces[currentPlayerNum]!!
            val opponent = game.players[-1 * currentPlayerNum]!!
            val inCheck = game.gameUtils.isCheck(kingPiece.second, opponent, game.board)
            game.gameUtils.cancelMove(game.players, currentPlayerNum, game.board, movePos, currentPos, game.capturedPiecesQueue)
            !inCheck
        }
        
        lastAvailableMoves = legalMoves
        view.displayAvailableMoves(lastAvailableMoves)
    }

    private fun attemptMove(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>) {
        val pieceNum = game.board[piecePos.first][piecePos.second]
        val player = game.players[game.currentPlayerColor]!!
        val pieceName = player.pieces[pieceNum]?.first ?: ""

        if (pieceName == "Pawn" && (movePos.first == 0 || movePos.first == 7)) {
            view.showPromotionDialog { choice ->
                completeMove(piecePos, movePos, choice)
            }
        } else {
            completeMove(piecePos, movePos)
        }
    }

    private fun completeMove(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>, promotionChoice: String = "Queen") {
        if (game.isEnd != 0) {
            view.displayWinner(game.isEnd)
            return
        }

        val pieceNum = game.board[piecePos.first][piecePos.second]
        val pieceName = game.playerWhite.pieces[pieceNum]?.first 
            ?: game.playerBlack.pieces[pieceNum]?.first 
            ?: ""
        
        game.makeMove(piecePos, movePos, promotionChoice)
        
        val moveStr = toNotation(piecePos, movePos, if (promotionChoice != "Queen") promotionChoice else pieceName)
        moveHistory.add(moveStr)
        view.updateMoveLog(moveHistory.joinToString(" "))

        lastAvailableMoves = listOf()
        view.clearSelection()
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces, game.currentPlayerColor)
        view.setLastMove(piecePos, movePos)

        if (game.isCheck[-1] == true) {
            view.displayCheck(-1)
        } else if (game.isCheck[1] == true) {
            view.displayCheck(1)
        } else {
            view.displayCheck(0) 
        }

        if (game.isEnd != 0) {
            view.displayWinner(game.isEnd)
        } else if (game.isAiEnabled && game.currentPlayerColor == 1) { 
            triggerAiMove()
        }
    }

    private fun triggerAiMove() {
        if (isAiThinking) return
        isAiThinking = true
        
        thread {
            val move = chessAI.getBestMove(game, 3) 
            
            Handler(Looper.getMainLooper()).post {
                isAiThinking = false
                if (move != null) {
                    // AI always promotes to Queen for now
                    completeMove(move.first, move.second)
                } else {
                    if (game.isEnd != 0) {
                        view.displayWinner(game.isEnd)
                    }
                }
            }
        }
    }

    private fun toNotation(from: Pair<Int, Int>, to: Pair<Int, Int>, pieceName: String): String {
        val files = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
        val ranks = arrayOf("8", "7", "6", "5", "4", "3", "2", "1")
        val pieceCode = when (pieceName) {
            "King" -> "K"
            "Queen" -> "Q"
            "Rook" -> "R"
            "Bishop" -> "B"
            "Knight" -> "N"
            else -> ""
        }
        return "$pieceCode${files[to.second]}${ranks[to.first]}"
    }

    interface ChessboardInterface {
        fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>)
        fun sendInputToPresenter(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?)
        fun clearSelection()
        fun setLastMove(from: Pair<Int, Int>?, to: Pair<Int, Int>?)
        fun updateMoveLog(moves: String)
        fun showPromotionDialog(callback: (String) -> Unit)
        fun redrawPieces(whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                         blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                         currentPlayer: Int)
        fun displayWinner(player: Int)
        fun displayCheck(player: Int)
    }
}