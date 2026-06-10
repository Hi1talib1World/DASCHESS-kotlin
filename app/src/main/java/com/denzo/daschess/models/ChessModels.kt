package com.denzo.daschess.models

data class UserStats(
    val name: String,
    val title: String,
    val rapidRating: Int,
    val globalRank: Int,
    val winRate: Double,
    val totalGames: Int,
    val bestWin: Int,
    val accuracy: Double
)

data class MatchHistoryItem(
    val id: String,
    val opponentName: String,
    val opponentRating: Int,
    val opponentTitle: String?,
    val gameType: String,
    val timeControl: String,
    val result: String, // "1-0", "0-1", "1/2-1/2"
    val timestamp: Long,
    val ratingChange: Int
)

data class ChessPuzzle(
    val id: String,
    val difficulty: String,
    val description: String,
    val boardState: String // FEN or similar
)