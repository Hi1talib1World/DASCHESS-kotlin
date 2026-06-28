package com.denzo.daschess

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denzo.daschess.models.ChessPuzzle

class PuzzlesAdapter(
    private var puzzles: List<ChessPuzzle>,
    private val onPuzzleClick: (ChessPuzzle) -> Unit
) : RecyclerView.Adapter<PuzzlesAdapter.PuzzleViewHolder>() {

    class PuzzleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.puzzle_title)
        val difficultyTextView: TextView = view.findViewById(R.id.puzzle_difficulty)
        val solveButton: Button = view.findViewById(R.id.btn_solve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_puzzle, parent, false)
        return PuzzleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {
        val puzzle = puzzles[position]
        holder.titleTextView.text = "Puzzle #${puzzle.id}"
        holder.difficultyTextView.text = puzzle.difficulty
        holder.solveButton.setOnClickListener { onPuzzleClick(puzzle) }
    }

    override fun getItemCount(): Int = puzzles.size

    fun updatePuzzles(newPuzzles: List<ChessPuzzle>) {
        this.puzzles = newPuzzles
        notifyDataSetChanged()
    }
}