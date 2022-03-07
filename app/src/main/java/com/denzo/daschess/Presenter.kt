package com.denzo.daschess

class Presenter (private val view: ChessboardInterface) {

    private var game = Game()

    // Variable of check state
    // 0: no check
    // 1: white has to move his king
    // -1: black has to move his king
    private var isCheck = 0

    privte var lastAvailableMoves: List<Pair<Int, Int>> = listOf()

    fun cancelMove(){
        game.cancelMove()
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces)
    }

    fun restartGame(){
        //Init new Game object with initial state of the game
        game = Game()
        // And redraw pieces on the board
        view.redrawPieces(game.playerWhite.pieces, game.plyerBlack.pieces)

    }

    fun handleInput(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?){

        var lastSelection = 0
        if (previousPosition != null){
            lastSelection = game.board[previousPosition.first][previousPosition.second]

        }
        val pieceNum = game.board[currentPosition !!.first][currentPosition.second]
    }

    val pieceNum = game.board[currentPosition!!.first][currentPosition.second]
    val currentPlayerNum = game.currentPlayerColor
    /* Handle the logic:
           -if chosen piece of current player's side -> tell view to select it and
            display available moves
           -if chosen pos is one of the available moves for previous pos and previous
           selection is piece of current player -> make move for piece on previous pos
           -else -> clear all selections and list of available positions */
    when {
        (pieceNum.sign == currentPlayerNum) -> SelectPieceToMove(pieceNum, game.players[currentPlayerNum])
        view.displayAvailableMoves(lastAvailableMoves)
    }

    private fun movePiece(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>){
        lastAvailableMoves = game.gameUtils.getAvailableMovesForPiece(pieceNum, game.players[currentPlayerNum])
        view.displayAvailableMoves(lastAvailableMoves)
    }
    private fun movePiece(picePos: Pai)
}