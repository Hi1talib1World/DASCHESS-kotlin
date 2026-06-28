package com.denzo.daschess.data

import com.denzo.daschess.models.ChessPuzzle
import com.denzo.daschess.models.MatchHistoryItem
import com.denzo.daschess.models.UserStats
import kotlinx.coroutines.delay

class ChessRepository {

    suspend fun getUserStats(): UserStats {
        delay(800) // Mock network delay
        return UserStats(
            name = "Magnus",
            title = "GRANDMASTER",
            rapidRating = 2842,
            globalRank = 1,
            winRate = 68.4,
            totalGames = 1250,
            bestWin = 2910,
            accuracy = 94.2
        )
    }

    suspend fun getRecentMatches(): List<MatchHistoryItem> {
        delay(1200)
        return listOf(
            MatchHistoryItem("1", "Stockfish_LVL8", 2200, null, "Blitz", "3+2", "1-0", System.currentTimeMillis() - 720000, 12),
            MatchHistoryItem("2", "Grandmaster_X", 2750, "GM", "Rapid", "10+5", "1/2-1/2", System.currentTimeMillis() - 7200000, 0),
            MatchHistoryItem("3", "KasparovFan99", 2680, null, "Bullet", "1+0", "0-1", System.currentTimeMillis() - 18000000, -8)
        )
    }

    suspend fun getDailyPuzzle(): ChessPuzzle {
        delay(500)
        return ChessPuzzle(
            id = "4291",
            difficulty = "DIFFICULT",
            description = "White to move and win",
            boardState = ""
        )
    }

    suspend fun getPuzzles(): List<ChessPuzzle> {
        delay(1000)
        return listOf(
            ChessPuzzle("1", "EASY", "Find the best move for White", ""),
            ChessPuzzle("2", "MEDIUM", "Checkmate in 2", ""),
            ChessPuzzle("3", "DIFFICULT", "Sacrifice and win", ""),
            ChessPuzzle("4", "EASY", "Defend the back rank", ""),
            ChessPuzzle("5", "MEDIUM", "Fork the King and Queen", "")
        )
    }
}