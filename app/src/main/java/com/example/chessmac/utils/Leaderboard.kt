package com.example.chessmac.utils

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmac.MainMenu
import com.example.chessmac.R
import com.example.chessmac.auth.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Leaderboard : ComponentActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.recyclerViewLeaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve data from firebase
        loadLeaderboardData { userScores ->
            adapter = LeaderboardAdapter(userScores)
            recyclerView.adapter = adapter
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Leaderboard, MainMenu::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun loadLeaderboardData(callback: (List<User>) -> Unit) {
        databaseReference = FirebaseDatabase.getInstance("https://chessmacc-3aaab-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("UsersScore")
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val leaderboardList = mutableListOf<User>()
                dataSnapshot.children.forEach { userSnapshot ->
                    val nickname = userSnapshot.child("nickname").getValue(String::class.java)
                    val profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String::class.java) // Retrieve profile image URL
                    val scoresSnapshot = userSnapshot.child("scores")
                    var highestScore = 0.0 // Initialize with 0 to have it as an entry in the firebase
                    var scoreDate = ""
                    scoresSnapshot.children.forEach { scoreSnapshot ->
                        val score = scoreSnapshot.child("score").getValue(Double::class.java) ?: 0.0
                        val date = scoreSnapshot.child("date").getValue(String::class.java) ?: ""
                        if (score > highestScore) {
                            highestScore = score
                            scoreDate = date
                        }
                    }
                    if (highestScore != 0.0 && nickname != null) {
                        val user = User(nickname, highestScore, scoreDate, profileImageUrl)
                        leaderboardList.add(user)
                    }
                }
                leaderboardList.sortByDescending { it.quizscore }
                callback(leaderboardList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Leaderboard", "Error loading leaderboard data: ${error.message}")
            }
        }
        databaseReference.addListenerForSingleValueEvent(listener)
    }

    }
