package com.example.chessmac.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chessmac.R
import com.example.chessmac.auth.User

class LeaderboardAdapter(private val userScores: List<User>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    // Drawable resources for top 3 positions
    private val topPositionDrawables = listOf(
        R.drawable.gold_medal,
        R.drawable.second_rank,
        R.drawable.third_place
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val positionImageView: ImageView = view.findViewById(R.id.positionImageView)
        val profileImageView: ImageView = view.findViewById(R.id.profileImageView)
        val nicknameTextView: TextView = view.findViewById(R.id.nicknameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
        val dateTextView: TextView = view.findViewById(R.id.DateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userScore = userScores[position]
        holder.nicknameTextView.text = userScore.nickname
        holder.scoreTextView.text = userScore.quizscore.toString()
        holder.dateTextView.text = userScore.scoreDate

        // Load profile image using Glide or Picasso
        Glide.with(holder.itemView)
            .load(userScore.profileImageUrl)
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Placeholder image if there's an error loading
            .circleCrop() // Crop the image into a circle
            .into(holder.profileImageView)

        // Set default position image
        holder.positionImageView.setImageResource(R.drawable.leaderboard_default)

        // Set specific position image for top 3 positions
        if (position < topPositionDrawables.size) {
            holder.positionImageView.setImageResource(topPositionDrawables[position])
        }
    }

    override fun getItemCount() = userScores.size
}

