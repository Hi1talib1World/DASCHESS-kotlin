package com.denzo.daschess.model.piece


class PieceFactoryImpl : PieceFactory {

    override fun makeNewWhiteBishop(): Bishop {
        return Bishop(Colour.WHITE)
    }

    override fun makeNewWhiteKing(): King {
        return King(Colour.WHITE)
    }

    override fun makeNewWhiteKnight(): Knight {
        return Knight(Colour.WHITE)
    }

    override fun makeNewWhitePawn(): Pawn {
        return Pawn(Colour.WHITE)
    }

    override fun makeNewWhiteQueen(): Queen {
        return Queen(Colour.WHITE)
    }

    override fun makeNewWhiteRook(): Rook {
        return Rook(Colour.WHITE)
    }

    override fun makeNewBlackBishop(): Bishop {
        return Bishop(Colour.BLACK)
    }

    override fun makeNewBlackKing(): King {
        return King(Colour.BLACK)
    }

    override fun makeNewBlackKnight(): Knight {
        return Knight(Colour.BLACK)
    }

    override fun makeNewBlackPawn(): Pawn {
        return Pawn(Colour.BLACK)
    }

    override fun makeNewBlackQueen(): Queen {
        return Queen(Colour.BLACK)
    }

    override fun makeNewBlackRook(): Rook {
        return Rook(Colour.BLACK)
    }

}