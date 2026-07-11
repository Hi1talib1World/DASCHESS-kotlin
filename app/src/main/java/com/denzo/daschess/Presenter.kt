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
        lastAvailableMoves = listOf()
        view.clearSelection()
        updateCapturedVisuals()
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
        val opponent = game.players[-1 * game.currentPlayerColor]!!
        val pieceName = player.pieces[pieceNum]?.first ?: ""

        // Check if move is legal (doesn't leave king in check)
        game.gameUtils.makeMove(game.players, game.currentPlayerColor, game.board, piecePos, movePos, game.capturedPiecesQueue)
        val kingPiece = player.pieces[game.currentPlayerColor]!!
        val inCheck = game.gameUtils.isCheck(kingPiece.second, opponent, game.board)
        game.gameUtils.cancelMove(game.players, game.currentPlayerColor, game.board, movePos, piecePos, game.capturedPiecesQueue)

        if (inCheck) {
            view.displayIllegalMove("Illegal move: King in check")
            view.clearSelection()
            return
        }

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
        val player = game.players[game.currentPlayerColor]!!
        val pieceName = player.pieces[pieceNum]?.first ?: ""
        val targetPiece = game.board[movePos.first][movePos.second]
        val isCapture = targetPiece != 0 || (pieceName == "Pawn" && piecePos.second != movePos.second)

        // Handle Castling Notation
        val moveStr = when {
            pieceName == "King" && movePos.second - piecePos.second == 2 -> "O-O"
            pieceName == "King" && movePos.second - piecePos.second == -2 -> "O-O-O"
            else -> {
                val notation = toNotation(piecePos, movePos, pieceName, isCapture)
                if (promotionChoice != "Queen" && pieceName == "Pawn" && (movePos.first == 0 || movePos.first == 7)) {
                    "$notation=${promotionChoice[0]}"
                } else notation
            }
        }
        
        game.makeMove(piecePos, movePos, promotionChoice)
        
        moveHistory.add(moveStr)
        view.updateMoveLog(moveHistory.joinToString(" "))

        lastAvailableMoves = listOf()
        view.clearSelection()
        updateCapturedVisuals()
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
            // Update ELO (Simple mock logic)
            if (!UserSession.isGuest) {
                if (game.isEnd == -1) UserSession.lastEloChange = 15
                else if (game.isEnd == 1) UserSession.lastEloChange = -15
            }
            view.displayWinner(game.isEnd)
        } else if (game.isAiEnabled && game.currentPlayerColor == 1) { 
            triggerAiMove()
        }
    }

    private fun triggerAiMove() {
        if (isAiThinking) return
        isAiThinking = true
        view.setAiThinking(true)
        
        thread {
            val move = chessAI.getBestMove(game, 3) 
            
            Handler(Looper.getMainLooper()).post {
                isAiThinking = false
                view.setAiThinking(false)
                if (move != null) {
                    completeMove(move.first, move.second)
                } else {
                    if (game.isEnd != 0) {
                        view.displayWinner(game.isEnd)
                    }
                }
            }
        }
    }

    private fun updateCapturedVisuals() {
        val capturedByWhite = game.capturedPiecesQueue.filter { it.first > 0 }.map { getPieceSymbol(it.second) }
        val capturedByBlack = game.capturedPiecesQueue.filter { it.first < 0 }.map { getPieceSymbol(it.second) }
        
        view.updateCapturedPieces(capturedByWhite.joinToString(""), capturedByBlack.joinToString(""))
    }

    private fun getPieceSymbol(name: String): String {
        return when (name) {
            "Pawn" -> "p"
            "Knight" -> "n"
            "Bishop" -> "b"
            "Rook" -> "r"
            "Queen" -> "q"
            else -> ""
        }
    }

    private fun toNotation(from: Pair<Int, Int>, to: Pair<Int, Int>, pieceName: String, isCapture: Boolean): String {
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
        
        return if (pieceName == "Pawn") {
            if (isCapture) "${files[from.second]}x${files[to.second]}${ranks[to.first]}"
            else "${files[to.second]}${ranks[to.first]}"
        } else {
            val capture = if (isCapture) "x" else ""
            "$pieceCode$capture${files[to.second]}${ranks[to.first]}"
        }
    }

    interface ChessboardInterface {
        fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>)
        fun sendInputToPresenter(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?)
        fun clearSelection()
        fun setLastMove(from: Pair<Int, Int>?, to: Pair<Int, Int>?)
        fun updateMoveLog(moves: String)
        fun updateCapturedPieces(whiteCaptured: String, blackCaptured: String)
        fun setAiThinking(isThinking: Boolean)
        fun displayIllegalMove(message: String)
        fun showPromotionDialog(callback: (String) -> Unit)
        fun redrawPieces(whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                         blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                         currentPlayer: Int)
        fun displayWinner(player: Int)
        fun displayCheck(player: Int)
    }
}