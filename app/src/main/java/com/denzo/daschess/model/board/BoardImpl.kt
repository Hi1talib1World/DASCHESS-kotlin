package com.denzo.daschess.model.board


import com.denzo.daschess.model.piece.Piece
import com.denzo.daschess.model.piece.PieceFactory

class BoardImpl(private val pieceFactory: PieceFactory) : Board {

    private var pieceGrid = Array<Array<Piece?>>(HEIGHT) {
        Array(WIDTH) { null }
    }

    override fun getGridElement(gridCoordinates: Pair<Int, Int>): Piece? {
        return pieceGrid[gridCoordinates.first][gridCoordinates.second]
    }

    override fun getHeight(): Int {
        return HEIGHT
    }

    override fun getWidth(): Int {
        return WIDTH
    }

    override fun initializeGrid() {

        var row = 0
        pieceGrid[row][0] = pieceFactory.makeNewBlackRook()
        pieceGrid[row][1] = pieceFactory.makeNewBlackKnight()
        pieceGrid[row][2] = pieceFactory.makeNewBlackBishop()
        pieceGrid[row][3] = pieceFactory.makeNewBlackQueen()
        pieceGrid[row][4] = pieceFactory.makeNewBlackKing()
        pieceGrid[row][5] = pieceFactory.makeNewBlackBishop()
        pieceGrid[row][6] = pieceFactory.makeNewBlackKnight()
        pieceGrid[row][7] = pieceFactory.makeNewBlackRook()

        row = 1
        for (column in 0 until WIDTH) {
            pieceGrid[row][column] = pieceFactory.makeNewBlackPawn()
        }

        row = 6
        for (column in 0 until WIDTH) {
            pieceGrid[row][column] = pieceFactory.makeNewWhitePawn()
        }

        row = 7
        pieceGrid[row][0] = pieceFactory.makeNewWhiteRook()
        pieceGrid[row][1] = pieceFactory.makeNewWhiteKnight()
        pieceGrid[row][2] = pieceFactory.makeNewWhiteBishop()
        pieceGrid[row][3] = pieceFactory.makeNewWhiteQueen()
        pieceGrid[row][4] = pieceFactory.makeNewWhiteKing()
        pieceGrid[row][5] = pieceFactory.makeNewWhiteBishop()
        pieceGrid[row][6] = pieceFactory.makeNewWhiteKnight()
        pieceGrid[row][7] = pieceFactory.makeNewWhiteRook()

    }

    override fun isOccupied(gridCoordinates: Pair<Int, Int>): Boolean {
        return pieceGrid[gridCoordinates.first][gridCoordinates.second] != null
    }

    override fun move(firstGridCoordinates: Pair<Int, Int>, secondGridCoordinates: Pair<Int, Int>): Boolean {

        val firstPiece = pieceGrid[firstGridCoordinates.first][firstGridCoordinates.second]
        val secondPiece = pieceGrid[secondGridCoordinates.first][secondGridCoordinates.second]

        return if (firstPiece?.colour == secondPiece?.colour) {

            false

        } else {

            pieceGrid[secondGridCoordinates.first][secondGridCoordinates.second] =
                pieceGrid[firstGridCoordinates.first][firstGridCoordinates.second]
            pieceGrid[firstGridCoordinates.first][firstGridCoordinates.second] = null

            true

        }

    }

    companion object {
        private const val HEIGHT = 8
        private const val WIDTH = 8
    }

}