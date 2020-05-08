package com.denzo.daschess.model.piece


interface PieceFactory {

    fun makeNewWhiteBishop(): Bishop
    fun makeNewWhiteKing(): King
    fun makeNewWhiteKnight(): Knight
    fun makeNewWhitePawn(): Pawn
    fun makeNewWhiteQueen(): Queen
    fun makeNewWhiteRook(): Rook

    fun makeNewBlackBishop(): Bishop
    fun makeNewBlackKing(): King
    fun makeNewBlackKnight(): Knight
    fun makeNewBlackPawn(): Pawn
    fun makeNewBlackQueen(): Queen
    fun makeNewBlackRook(): Rook

}