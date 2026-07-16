package com.denzo.daschess

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.denzo.daschess.customviews.ChessboardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class GameFragment : Fragment(), Presenter.ChessboardInterface, ChessboardView.OnSquareSelectedListener {

    private lateinit var chessboard: ChessboardView
    private lateinit var presenter: Presenter
    
    private var whiteTimer: CountDownTimer? = null
    private var blackTimer: CountDownTimer? = null
    private var whiteTimeLeft = 3600000L // 60 mins for testing
    private var blackTimeLeft = 3600000L
    
    private lateinit var whiteTimerText: TextView
    private lateinit var blackTimerText: TextView
    private lateinit var moveLogText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chessboard = view.findViewById(R.id.chessboard)
        whiteTimerText = view.findViewById(R.id.player_timer)
        blackTimerText = view.findViewById(R.id.opponent_timer)
        moveLogText = view.findViewById(R.id.move_log)

        chessboard.listener = this
        presenter = Presenter(this)
        
        val isAiEnabled = arguments?.getBoolean("isAiEnabled") ?: false
        presenter.restartGame(isAiEnabled)

        view.findViewById<Button>(R.id.btn_undo).setOnClickListener {
            presenter.cancelMove()
        }
        
        view.findViewById<Button>(R.id.btn_resign).setOnClickListener {
            displayWinner(1) // Opponent wins
        }

        startTimer(true) 
    }

    // --- OnSquareSelectedListener ---

    private var firstSquare: Pair<Int, Int>? = null

    override fun onSquareSelected(row: Int, col: Int) {
        if (firstSquare == null) {
            val selected = presenter.handleInput(Pair(row, col), null)
            if (selected) {
                firstSquare = Pair(row, col)
            }
        } else {
            presenter.handleInput(Pair(row, col), firstSquare)
            firstSquare = null
        }
    }

    override fun onMoveAttempted(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        presenter.handleInput(Pair(toRow, toCol), Pair(fromRow, fromCol))
        firstSquare = null
    }

    // --- ChessboardInterface ---

    override fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>) {
        chessboard.currentChosenPos?.let { chessboard.displaySelection(it.first, it.second) }
        chessboard.displayAvailableMoves(movesCoordinates)
    }

    override fun clearSelection() {
        chessboard.clearSelection()
        firstSquare = null
    }

    override fun setLastMove(from: Pair<Int, Int>?, to: Pair<Int, Int>?) {
        chessboard.setLastMove(from, to)
    }

    override fun updateMoveLog(moves: String) {
        moveLogText.text = "Moves: $moves"
    }

    override fun updateCapturedPieces(whiteCaptured: String, blackCaptured: String) {
        view?.findViewById<TextView>(R.id.player_captured)?.text = whiteCaptured
        view?.findViewById<TextView>(R.id.opponent_captured)?.text = blackCaptured
    }

    override fun setAiThinking(isThinking: Boolean) {
        view?.findViewById<View>(R.id.tv_ai_thinking)?.visibility = if (isThinking) View.VISIBLE else View.GONE
    }

    override fun displayIllegalMove(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun showPromotionDialog(callback: (String) -> Unit) {
        val options = arrayOf("Queen", "Rook", "Bishop", "Knight")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Promote Pawn to:")
            .setItems(options) { _, which ->
                callback(options[which])
            }
            .setCancelable(false)
            .show()
    }

    override fun redrawPieces(
        whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
        blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
        currentPlayer: Int
    ) {
        chessboard.redrawPieces(whitePieces, blackPieces)
        
        // Visual turn indicator
        if (currentPlayer == -1) {
            view?.findViewById<View>(R.id.player_info)?.alpha = 1.0f
            view?.findViewById<View>(R.id.opponent_info)?.alpha = 0.6f
        } else {
            view?.findViewById<View>(R.id.player_info)?.alpha = 0.6f
            view?.findViewById<View>(R.id.opponent_info)?.alpha = 1.0f
        }

        // Reset timers only if move log is empty (start of game)
        if (moveLogText.text == "Moves: ") {
            whiteTimeLeft = 3600000L
            blackTimeLeft = 3600000L
            updateTimerText(whiteTimerText, whiteTimeLeft)
            updateTimerText(blackTimerText, blackTimeLeft)
        }

        startTimer(currentPlayer == -1)
    }

    override fun displayWinner(player: Int) {
        whiteTimer?.cancel()
        blackTimer?.cancel()
        val message = when (player) {
            -1 -> "White wins!"
            1 -> "Black wins!"
            2 -> "Stalemate!"
            else -> "Game Over!"
        }
        view?.let { Snackbar.make(it.findViewById(R.id.chessboard), message, Snackbar.LENGTH_INDEFINITE)
            .setAction("NEW GAME") { (activity as? MainActivity)?.startNewGame() }
            .show() 
        }
    }

    override fun displayCheck(player: Int) {
        if (player == 0) {
            chessboard.displayCheckSquare(null)
            return
        }
        
        val vibrator = requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
        
        // Pattern based on who is in check
        if (player == -1) { // White (You)
            // Long urgent vibration
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }
        } else { // Black (AI)
            // Short notification
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(100)
            }
        }

        val pieces = if (player == -1) chessboard.whitePlayerPieces else chessboard.blackPlayerPieces
        val kingPos = pieces[player]?.second
        chessboard.displayCheckSquare(kingPos)
    }

    // --- Timers ---

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
                    displayWinner(1)
                }
            }.start()
        } else {
            blackTimer = object : CountDownTimer(blackTimeLeft, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    blackTimeLeft = millisUntilFinished
                    updateTimerText(blackTimerText, millisUntilFinished)
                }
                override fun onFinish() {
                    displayWinner(-1)
                }
            }.start()
        }
    }

    private fun updateTimerText(textView: TextView, millis: Long) {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        textView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        
        // Color feedback based on time remaining
        if (millis < 30000) { // Less than 30 seconds
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.chess_loss))
            if (millis < 10000 && (millis / 1000) % 2 == 0L) {
                textView.alpha = 0.5f // Blink effect
            } else {
                textView.alpha = 1.0f
            }
        } else if (millis < 60000) { // Less than 1 minute
            textView.setTextColor(android.graphics.Color.parseColor("#FFA500")) // Orange
            textView.alpha = 1.0f
        } else {
            if (textView.id == R.id.player_timer) {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.chess_primary))
            }
            textView.alpha = 1.0f
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        whiteTimer?.cancel()
        blackTimer?.cancel()
    }

    override fun sendInputToPresenter(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?) {
        // Legacy, handled by listener now
    }
}