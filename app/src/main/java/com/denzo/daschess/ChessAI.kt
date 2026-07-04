package com.denzo.daschess

import kotlin.math.max
import kotlin.math.min

class ChessAI {

    private val pieceValues = mapOf(
        "Pawn" to 10,
        "Knight" to 30,
        "Bishop" to 30,
        "Rook" to 50,
        "Queen" to 90,
        "King" to 900
    )

    fun getBestMove(game: Game, depth: Int): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        val color = game.currentPlayerColor
        val isMaximizing = color == 1 
        
        var bestScore = if (isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE
        var bestMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
        
        val player = game.players[color] ?: return null
        player.updateAvailableMoves(game.board, game.lastMoveCurrentPos, game.lastMovePreviousPos, game.lastMovedPieceNum)
        
        val legalMoves = getAllLegalMoves(game, color)
        if (legalMoves.isEmpty()) return null

        for (move in legalMoves) {
            game.gameUtils.makeMove(game.players, color, game.board, move.first, move.second, game.capturedPiecesQueue)
            val score = minimax(game, depth - 1, Int.MIN_VALUE, Int.MAX_VALUE, !isMaximizing, move.second, move.first, game.board[move.second.first][move.second.second])
            game.gameUtils.cancelMove(game.players, color, game.board, move.second, move.first, game.capturedPiecesQueue)
            
            if (isMaximizing) {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            } else {
                if (score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }
        
        return bestMove
    }

    private fun minimax(game: Game, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean, lastMoveCurr: Pair<Int, Int>?, lastMovePrev: Pair<Int, Int>?, lastPiece: Int): Int {
        if (depth == 0) {
            return evaluateBoard(game)
        }

        val color = if (isMaximizing) 1 else -1
        val player = game.players[color]!!
        player.updateAvailableMoves(game.board, lastMoveCurr, lastMovePrev, lastPiece)
        
        val legalMoves = getAllLegalMoves(game, color)
        
        if (legalMoves.isEmpty()) {
            val opponent = game.players[-1 * color]!!
            val kingPiece = player.pieces[color] ?: return 0
            if (game.gameUtils.isCheck(kingPiece.second, opponent, game.board)) {
                return if (isMaximizing) -10000 else 10000 
            }
            return 0 
        }

        var mutableAlpha = alpha
        var mutableBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in legalMoves) {
                val p = game.board[move.first.first][move.first.second]
                game.gameUtils.makeMove(game.players, color, game.board, move.first, move.second, game.capturedPiecesQueue)
                val eval = minimax(game, depth - 1, mutableAlpha, mutableBeta, false, move.second, move.first, p)
                game.gameUtils.cancelMove(game.players, color, game.board, move.second, move.first, game.capturedPiecesQueue)
                
                maxEval = max(maxEval, eval)
                mutableAlpha = max(mutableAlpha, eval)
                if (mutableBeta <= mutableAlpha) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in legalMoves) {
                val p = game.board[move.first.first][move.first.second]
                game.gameUtils.makeMove(game.players, color, game.board, move.first, move.second, game.capturedPiecesQueue)
                val eval = minimax(game, depth - 1, mutableAlpha, mutableBeta, true, move.second, move.first, p)
                game.gameUtils.cancelMove(game.players, color, game.board, move.second, move.first, game.capturedPiecesQueue)
                
                minEval = min(minEval, eval)
                mutableBeta = min(mutableBeta, eval)
                if (mutableBeta <= mutableAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateBoard(game: Game): Int {
        var score = 0
        for (player in game.players.values) {
            for (piece in player.pieces.values) {
                val value = pieceValues[piece.first] ?: 0
                score += value * player.color
            }
        }
        return score
    }

    private fun getAllLegalMoves(game: Game, color: Int): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        val player = game.players[color] ?: return emptyList()
        val opponent = game.players[-1 * color] ?: return emptyList()
        val legalMoves = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        
        val movesToTry = player.availableMoves.map { it.key to it.value }
        for ((pieceNum, moves) in movesToTry) {
            val fromPos = player.pieces[pieceNum]?.second ?: continue
            for (toPos in moves) {
                game.gameUtils.makeMove(game.players, color, game.board, fromPos, toPos, game.capturedPiecesQueue)
                val kingPiece = player.pieces[color] ?: continue
                val inCheck = game.gameUtils.isCheck(kingPiece.second, opponent, game.board)
                game.gameUtils.cancelMove(game.players, color, game.board, toPos, fromPos, game.capturedPiecesQueue)
                
                if (!inCheck) {
                    legalMoves.add(Pair(fromPos, toPos))
                }
            }
        }
        return legalMoves
    }
}