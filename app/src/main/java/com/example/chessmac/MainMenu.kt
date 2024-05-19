package com.example.chessmac

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessmac.ui.theme.ChessMACTheme

class MainMenu : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessMACTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Menu(this)
                }
            }
        }
    }
}

@Composable
fun Menu(activity: ComponentActivity) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val intent = Intent(activity, LocalGame::class.java)
            activity.startActivity(intent) }) {
            Text("Local Game")

        }
        Button(onClick = {
            val intent = Intent(activity, StockGame::class.java)
            activity.startActivity(intent) }) {
            Text("Stockfish Game")
        }
        Button(onClick = {
            val intent = Intent(activity, Quiz::class.java)
            activity.startActivity(intent) }) {
            Text("Quiz")
        }
        Button(onClick = {
            val intent = Intent(activity, Leaderboard::class.java)
            activity.startActivity(intent) }) {
            Text("Leaderboard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreview() {
    ChessMACTheme {
        MainMenu()
    }
}