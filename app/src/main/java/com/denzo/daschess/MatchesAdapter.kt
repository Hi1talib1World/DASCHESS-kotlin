package com.denzo.daschess

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denzo.daschess.models.MatchHistoryItem

class MatchesAdapter(private var matches: List<MatchHistoryItem>) :
    RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val opponentTextView: TextView = view.findViewById(R.id.match_name)
        val detailsTextView: TextView = view.findViewById(R.id.match_details)
        val scoreTextView: TextView = view.findViewById(R.id.match_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.opponentTextView.text = "${match.opponentName} ${match.opponentRating}"
        holder.detailsTextView.text = "${match.gameType} • ${match.timeControl}"
        holder.scoreTextView.text = match.result
    }

    override fun getItemCount(): Int = matches.size

    fun updateMatches(newMatches: List<MatchHistoryItem>) {
        this.matches = newMatches
        notifyDataSetChanged()
    }
}