package com.denzo.daschess

import kotlin.math.max
import kotlin.math.min

class ChessAI {

    private val pieceValues = mapOf(
        "Pawn" to 100,
        "Knight" to 320,
        "Bishop" to 330,
        "Rook" to 500,
        "Queen" to 900,
        "King" to 20000
    )

    private val pawnPST = arrayOf(
        intArrayOf(0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(50, 50, 50, 50, 50, 50, 50, 50),
        intArrayOf(10, 10, 20, 30, 30, 20, 10, 10),
        intArrayOf(5,  5, 10, 25, 25, 10,  5,  5),
        intArrayOf(0,  0,  0, 20, 20,  0,  0,  0),
        intArrayOf(5, -5,-10,  0,  0,-10, -5,  5),
        intArrayOf(5, 10, 10,-20,-20, 10, 10,  5),
        intArrayOf(0,  0,  0,  0,  0,  0,  0,  0)
    )

    private val knightPST = arrayOf(
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50),
        intArrayOf(-40,-20,  0,  0,  0,  0,-20,-40),
        intArrayOf(-30,  0, 10, 15, 15, 10,  0,-30),
        intArrayOf(-30,  5, 15, 20, 20, 15,  5,-30),
        intArrayOf(-30,  0, 15, 20, 20, 15,  0,-30),
        intArrayOf(-30,  5, 10, 15, 15, 10,  5,-30),
        intArrayOf(-40,-20,  0,  5,  5,  0,-20,-40),
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50)
    )

    private val bishopPST = arrayOf(
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5, 10, 10,  5,  0,-10),
        intArrayOf(-10,  5,  5, 10, 10,  5,  5,-10),
        intArrayOf(-10,  0, 10, 10, 10, 10,  0,-10),
        intArrayOf(-10, 10, 10, 10, 10, 10, 10,-10),
        intArrayOf(-10,  5,  0,  0,  0,  0,  5,-10),
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20)
    )

    private val rookPST = arrayOf(
        intArrayOf(0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(5, 10, 10, 10, 10, 10, 10,  5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(0,  0,  0,  5,  5,  0,  0,  0)
    )

    private val queenPST = arrayOf(
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5,  5,  5,  5,  0,-10),
        intArrayOf(-5,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(0,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(-10,  5,  5,  5,  5,  5,  0,-10),
        intArrayOf(-10,  0,  5,  0,  0,  0,  0,-10),
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20)
    )

    private val kingPST = arrayOf(
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-20,-30,-30,-40,-40,-30,-30,-20),
        intArrayOf(-10,-20,-20,-20,-20,-20,-20,-10),
        intArrayOf(20, 20,  0,  0,  0,  0, 20, 20),
        intArrayOf(20, 30, 10,  0,  0, 10, 30, 20)
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

        // Sort moves: simple heuristic, prioritize captures
        val sortedMoves = legalMoves.sortedByDescending { move ->
            val target = game.board[move.second.first][move.second.second]
            if (target != 0) Math.abs(target) else 0
        }

        for (move in sortedMoves) {
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
                return if (isMaximizing) -30000 + (3 - depth) else 30000 - (3 - depth) 
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
                val name = piece.first
                val pos = piece.second
                val baseValue = pieceValues[name] ?: 0
                
                // Position bonus
                val pst = when (name) {
                    "Pawn" -> pawnPST
                    "Knight" -> knightPST
                    "Bishop" -> bishopPST
                    "Rook" -> rookPST
                    "Queen" -> queenPST
                    "King" -> kingPST
                    else -> null
                }
                
                var bonus = 0
                if (pst != null) {
                    // Flip PST for white (color -1)
                    val r = if (player.color == -1) 7 - pos.first else pos.first
                    val c = pos.second
                    bonus = pst[r][c]
                }
                
                score += (baseValue + bonus) * player.color
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