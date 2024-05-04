package com.example.chessmac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chessmac.ui.chessGame.ChessGameScreen

class LocalGame : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessGameScreen("LOCAL", context = applicationContext)
        }
    }

}


