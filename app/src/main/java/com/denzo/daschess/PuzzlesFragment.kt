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

class PuzzlesFragment : Fragment() {

    private lateinit var viewModel: PuzzlesViewModel
    private lateinit var puzzlesAdapter: PuzzlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_puzzles, container, false)

        val repository = ChessRepository()
        val factory = PuzzlesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PuzzlesViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupObservers(view)
        setupClickListeners(view)

        viewModel.loadPuzzlesData()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_puzzles)
        puzzlesAdapter = PuzzlesAdapter(emptyList()) { puzzle ->
            Toast.makeText(context, "Solving Puzzle #${puzzle.id}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = puzzlesAdapter
    }

    private fun setupObservers(view: View) {
        viewModel.featuredPuzzle.observe(viewLifecycleOwner, Observer { puzzle ->
            updateFeaturedPuzzleUI(view, puzzle)
        })

        viewModel.puzzleList.observe(viewLifecycleOwner, Observer { puzzles ->
            puzzlesAdapter.updatePuzzles(puzzles)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            view.findViewById<View>(R.id.loading_overlay)?.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        })
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<Button>(R.id.btn_solve_featured).setOnClickListener {
            val puzzleId = viewModel.featuredPuzzle.value?.id ?: "Daily"
            Toast.makeText(context, "Solving Puzzle #$puzzleId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFeaturedPuzzleUI(view: View, puzzle: ChessPuzzle) {
        view.findViewById<TextView>(R.id.featured_description).text = puzzle.description
        view.findViewById<TextView>(R.id.featured_difficulty).text = puzzle.difficulty
    }
}

class PuzzlesViewModelFactory(private val repository: ChessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PuzzlesViewModel(repository) as T
    }
}