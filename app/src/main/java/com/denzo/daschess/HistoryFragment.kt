package com.denzo.daschess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.denzo.daschess.data.ChessRepository
import com.denzo.daschess.models.MatchHistoryItem

class HistoryFragment : Fragment() {
    
    private lateinit var viewModel: HomeViewModel // Reusing HomeViewModel for stats

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        
        val repository = ChessRepository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupObservers(view)
        viewModel.loadDashboardData()
        
        view.findViewById<View>(R.id.btn_load_more)?.setOnClickListener {
            Toast.makeText(context, "Loading more matches...", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers(view: View) {
        viewModel.userStats.observe(viewLifecycleOwner, Observer { stats ->
            view.findViewById<TextView>(R.id.win_rate_value)?.text = "${stats.winRate}%"
            view.findViewById<TextView>(R.id.rating_value_history)?.text = stats.rapidRating.toString()
        })
        
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // show/hide skeleton or spinner
        })
    }
}