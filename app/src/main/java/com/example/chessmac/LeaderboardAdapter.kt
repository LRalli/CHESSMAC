package com.example.chessmac

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(private val userScores: List<User>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nicknameTextView: TextView = view.findViewById(R.id.nicknameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userScore = userScores[position]
        holder.nicknameTextView.text = userScore.nickname
        holder.scoreTextView.text = userScore.quizscore.toString()
    }

    override fun getItemCount() = userScores.size
}

