package com.denzo.daschess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.ChessPuzzle
import com.denzo.daschess.models.MatchHistoryItem
import com.denzo.daschess.models.UserStats

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var matchesAdapter: MatchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Manual DI for simplicity, normally use Koin
        val repository = ChessRepository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupObservers(view)
        setupClickListeners(view)
        
        viewModel.loadDashboardData()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_recent_matches)
        matchesAdapter = MatchesAdapter(emptyList()) { match ->
            Toast.makeText(context, "Reviewing match vs ${match.opponentName}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = matchesAdapter
    }

    private fun setupObservers(view: View) {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { stats ->
            updateUserStatsUI(view, stats)
        })

        viewModel.recentMatches.observe(viewLifecycleOwner, Observer { matches ->
            updateMatchesUI(matches)
        })

        viewModel.dailyPuzzle.observe(viewLifecycleOwner, Observer { puzzle ->
            updatePuzzleUI(view, puzzle)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // Update loading UI (e.g., show/hide a progress bar or skeleton)
            view.findViewById<View>(R.id.loading_overlay)?.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        })
    }

    private fun setupClickListeners(view: View) {
        val btnNewGame = view.findViewById<Button>(R.id.btn_new_game)
        btnNewGame.setOnClickListener {
            (activity as? MainActivity)?.startNewGame()
        }

        view.findViewById<Button>(R.id.btn_train_ai).setOnClickListener {
            (activity as? MainActivity)?.startNewGame(isAiEnabled = true)
        }
        
        view.findViewById<Button>(R.id.btn_solve_now).setOnClickListener {
            (activity as? MainActivity)?.navigateToPuzzles()
        }

        view.findViewById<TextView>(R.id.tv_view_all_matches).setOnClickListener {
            (activity as? MainActivity)?.navigateToHistory()
        }
    }

    private fun updateUserStatsUI(view: View, stats: UserStats) {
        val name = if (UserSession.isGuest) {
            "Guest Player"
        } else {
            UserSession.userName
        }
        
        val welcomeText = if (stats.rapidRating > 2500) {
            "Welcome back, Grandmaster $name."
        } else if (stats.rapidRating > 1500) {
            "Welcome back, $name."
        } else {
            "Welcome, $name. Ready to train?"
        }

        view.findViewById<TextView>(R.id.welcome_text).text = welcomeText
        view.findViewById<TextView>(R.id.rating_value).text = stats.rapidRating.toString()
        view.findViewById<TextView>(R.id.rank_value).text = "#${stats.globalRank}"
        view.findViewById<TextView>(R.id.user_title_badge).text = "${stats.title.uppercase()} STATUS"
    }

    private fun updatePuzzleUI(view: View, puzzle: ChessPuzzle) {
        view.findViewById<TextView>(R.id.puzzle_difficulty).text = puzzle.difficulty.uppercase()
        view.findViewById<TextView>(R.id.puzzle_description).text = puzzle.description
    }

    private fun updateMatchesUI(matches: List<MatchHistoryItem>) {
        matchesAdapter.updateMatches(matches)
    }
}

// Simple Factory for ViewModel
class HomeViewModelFactory(private val repository: ChessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}