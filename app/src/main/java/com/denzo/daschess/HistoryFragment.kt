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
import com.denzo.daschess.models.MatchHistoryItem

class HistoryFragment : Fragment() {
    
    private lateinit var viewModel: HistoryViewModel
    private lateinit var matchesAdapter: MatchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        
        val repository = ChessRepository()
        val factory = HistoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HistoryViewModel::class.java)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
        setupObservers(view)
        
        viewModel.loadHistoryData()
        
        view.findViewById<Button>(R.id.btn_load_more)?.setOnClickListener {
            Toast.makeText(context, "Loading more matches...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_history_matches)
        matchesAdapter = MatchesAdapter(emptyList()) { match ->
            Toast.makeText(context, "Analyzing match vs ${match.opponentName}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = matchesAdapter
    }
    
    private fun setupObservers(view: View) {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { stats ->
            view.findViewById<TextView>(R.id.win_rate_value)?.text = "${stats.winRate}%"
            view.findViewById<TextView>(R.id.rating_value_history)?.text = stats.rapidRating.toString()
        })

        viewModel.historyMatches.observe(viewLifecycleOwner, Observer { matches ->
            matchesAdapter.updateMatches(matches)
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            view.findViewById<View>(R.id.loading_overlay)?.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        })
    }
}

class HistoryViewModelFactory(private val repository: ChessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(repository) as T
    }
}