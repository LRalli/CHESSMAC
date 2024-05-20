package com.example.chessmac

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chessmac.ui.chessGame.ChessGameScreen
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

        loadLeaderboardData { userScores ->
            adapter = LeaderboardAdapter(userScores)
            recyclerView.adapter = adapter
        }
    }

    private fun loadLeaderboardData(callback: (List<User>) -> Unit) {
        databaseReference = FirebaseDatabase.getInstance("https://chessmacc-3aaab-default-rtdb.europe-west1.firebasedatabase.app").getReference("UsersScore")
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val leaderboardList = mutableListOf<User>()
                dataSnapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        leaderboardList.add(user)
                    }}
                // Ordinare la lista per punteggio in modo decrescente
                leaderboardList.sortByDescending { it.quizScore }
                callback(leaderboardList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }
        databaseReference.addValueEventListener(listener)
    }
    }
