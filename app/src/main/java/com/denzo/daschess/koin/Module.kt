package com.denzo.daschess.koin

import com.denzo.daschess.controller.SelectionController
import com.denzo.daschess.model.board.Board
import com.denzo.daschess.model.board.BoardImpl
import com.denzo.daschess.model.piece.PieceFactory
import com.denzo.daschess.model.piece.PieceFactoryImpl
import com.denzo.daschess.model.selection.Selection
import com.denzo.daschess.model.selection.SelectionImpl
import com.denzo.daschess.view.CoordinateMapper
import com.denzo.daschess.view.board.BoardRenderer
import com.denzo.daschess.view.board.BoardRendererImpl
import com.denzo.daschess.view.piece.PieceRenderer
import com.denzo.daschess.view.piece.PieceRendererImpl
import org.koin.dsl.module


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