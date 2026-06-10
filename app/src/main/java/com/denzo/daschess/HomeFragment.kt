package com.denzo.daschess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.MatchHistoryItem
import com.denzo.daschess.models.UserStats

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var loadingIndicator: View // We'll add this to XML

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

        setupObservers(view)
        setupClickListeners(view)
        
        viewModel.loadDashboardData()
    }

    private fun setupObservers(view: View) {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { stats ->
            updateUserStatsUI(view, stats)
        })

        viewModel.recentMatches.observe(viewLifecycleOwner, Observer { matches ->
            updateMatchesUI(view, matches)
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
        view.findViewById<Button>(R.id.btn_new_game).setOnClickListener {
            it.isEnabled = false // Micro-feedback: prevent double clicks
            (activity as? MainActivity)?.startNewGame()
        }

        view.findViewById<Button>(R.id.btn_train_ai).setOnClickListener {
            Toast.makeText(context, "AI Training coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<Button>(R.id.btn_solve_now).setOnClickListener {
            Toast.makeText(context, "Loading Puzzle...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserStatsUI(view: View, stats: UserStats) {
        view.findViewById<TextView>(R.id.welcome_text).text = "Welcome back, ${stats.name}."
        view.findViewById<TextView>(R.id.rating_value).text = stats.rapidRating.toString()
        view.findViewById<TextView>(R.id.rank_value).text = "#${stats.globalRank}"
    }

    private fun updateMatchesUI(view: View, matches: List<MatchHistoryItem>) {
        // In a real app, this would be a RecyclerView. 
        // For this refactor, we'll update the first two placeholders.
        if (matches.isNotEmpty()) {
            val match1 = matches[0]
            view.findViewById<TextView>(R.id.match1_name).text = "${match1.opponentName} ${match1.opponentRating}"
            view.findViewById<TextView>(R.id.match1_details).text = "${match1.gameType} • ${match1.timeControl}"
            view.findViewById<TextView>(R.id.match1_score).text = match1.result
        }
    }
}

// Simple Factory for ViewModel
class HomeViewModelFactory(private val repository: ChessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}