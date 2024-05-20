package com.example.chessmac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chessmac.ui.chessGame.ChessGameScreen
class Quiz : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessGameScreen("QUIZ", context = applicationContext, backgroundResId = R.drawable.perfect_green_grass)
        }
    }
}