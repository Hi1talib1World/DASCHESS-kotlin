package com.denzo.daschess

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.denzo.daschess.customviews.ChessboardView
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class GameFragment : Fragment(), Presenter.ChessboardInterface {

    private lateinit var chessboard: ChessboardView
    private lateinit var presenter: Presenter
    
    private var whiteTimer: CountDownTimer? = null
    private var blackTimer: CountDownTimer? = null
    private var whiteTimeLeft = 600000L // 10 mins
    private var blackTimeLeft = 600000L
    
    private lateinit var whiteTimerText: TextView
    private lateinit var blackTimerText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = Presenter(this)
        chessboard = view.findViewById(R.id.chessboard)
        whiteTimerText = view.findViewById(R.id.player_timer)
        blackTimerText = view.findViewById(R.id.opponent_timer)

        view.findViewById<Button>(R.id.btn_undo).setOnClickListener {
            presenter.cancelMove()
            // In a real game, switching timers back would be needed
        }
        
        view.findViewById<Button>(R.id.btn_resign).setOnClickListener {
            displayWinner(1) // Opponent wins
        }

        startTimer(true) // Start white's timer initially
    }

    private fun startTimer(isWhite: Boolean) {
        whiteTimer?.cancel()
        blackTimer?.cancel()
        
        if (isWhite) {
            whiteTimer = object : CountDownTimer(whiteTimeLeft, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    whiteTimeLeft = millisUntilFinished
                    updateTimerText(whiteTimerText, millisUntilFinished)
                }
                override fun onFinish() {
                    displayWinner(1) // Black wins on time
                }
            }.start()
        } else {
            blackTimer = object : CountDownTimer(blackTimeLeft, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    blackTimeLeft = millisUntilFinished
                    updateTimerText(blackTimerText, millisUntilFinished)
                }
                override fun onFinish() {
                    displayWinner(-1) // White wins on time
                }
            }.start()
        }
    }

    private fun updateTimerText(textView: TextView, millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        textView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun sendInputToPresenter(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?) {
        presenter.handleInput(currentPosition, previousPosition)
    }

    override fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>) {
        chessboard.displaySelection()
        chessboard.displayAvailableMoves(movesCoordinates)
    }

    override fun clearSelection() {
        chessboard.clearSelection()
    }

    override fun redrawPieces(
        whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
        blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>
    ) {
        chessboard.redrawPieces(whitePieces, blackPieces)
        chessboard.clearSelection()
        
        // Switch timers on move
        // This is a simplified check - in a real app, track the turn in Game model
        // startTimer(isWhiteTurn) 
    }

    override fun displayWinner(player: Int) {
        whiteTimer?.cancel()
        blackTimer?.cancel()
        val winnerColorString = if (player == -1) "White player" else "Black player"
        view?.let { Snackbar.make(it.findViewById(R.id.chessboard), "$winnerColorString wins!", Snackbar.LENGTH_INDEFINITE)
            .setAction("NEW GAME") { (activity as? MainActivity)?.startNewGame() }
            .show() 
        }
    }

    override fun displayCheck(player: Int) {
        val kingInCheckColor = if (player == -1) "White" else "Black"
        view?.let { Snackbar.make(it.findViewById(R.id.chessboard), "$kingInCheckColor king in check!", Snackbar.LENGTH_SHORT).show() }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        whiteTimer?.cancel()
        blackTimer?.cancel()
    }
}