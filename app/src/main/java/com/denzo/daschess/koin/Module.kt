package com.denzo.daschess.koin

import com.denzo.daschess.model.board.Board
import com.denzo.daschess.model.board.BoardImpl


class Module {

    companion object {

        val appModule = module {

            single<Board> { BoardImpl(get()) }
            single { SelectionController(get(), get()) }
            single<BoardRenderer> { BoardRendererImpl(get(), get(), get(), get()) }
            single { CoordinateMapper(get()) }
            single<PieceFactory> { PieceFactoryImpl() }
            single<PieceRenderer> { PieceRendererImpl(get(), get()) }
            single<Selection> { SelectionImpl(get()) }

        }

    }

}