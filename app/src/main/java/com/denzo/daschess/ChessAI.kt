package com.denzo.daschess

class ChessAI {
    /**
     * Generates a random legal move for the current player.
     * Returns a Pair of (fromPosition, toPosition), or null if no moves are available.
     */
    fun getRandomMove(game: Game): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        val currentPlayer = game.players[game.currentPlayerColor] ?: return null
        val legalMoves = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        for ((pieceNum, moves) in currentPlayer.availableMoves) {
            val fromPos = currentPlayer.pieces[pieceNum]?.second ?: continue
            for (toPos in moves) {
                legalMoves.add(Pair(fromPos, toPos))
            }
        }

        if (legalMoves.isEmpty()) return null
        
        return legalMoves.random()
    }
}