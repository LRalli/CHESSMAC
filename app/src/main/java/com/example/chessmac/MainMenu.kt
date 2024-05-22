package com.example.chessmac

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chessmac.auth.UserProfile
import com.example.chessmac.utils.Leaderboard
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val customFontFamily = FontFamily(
    Font(R.font.futura_medium_bt, FontWeight.Normal),
)

class MainMenu : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    elevation = 8.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val pokerMatTexture: Painter = painterResource(id = R.drawable.perfect_green_grass)
                        Menu(this@MainMenu, pokerMatTexture)
                    }
                }
            }
        }
    }
}

@Composable
fun Menu(activity: ComponentActivity, pokerMatTexture: Painter) {

    val customButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color(0xFF3A5730)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = pokerMatTexture,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp)) // Adjust this value as needed

            Box { // Stack the two images
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.Center)
                )
                Image(
                    painter = painterResource(id = R.drawable.test_4),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.Center)
                        .offset(y = 150.dp) // Adjust this value as needed
                )
            }
            Spacer(modifier = Modifier.height(70.dp)) // Add space between image and buttons
            Button(
                onClick = {
                    val intent = Intent(activity, LocalGame::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(270.dp)
                    .height(55.dp),
                colors = customButtonColors
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.handshake),
                        contentDescription = "Local Game Image",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Local Game",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f))
                }
            }
            Button(
                onClick = {
                    val intent = Intent(activity, StockGame::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(270.dp)
                    .height(55.dp),
                colors = customButtonColors
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.robot),
                        contentDescription = "StockFish",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Stockfish Game",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f))
                }
            }
            Button(
                onClick = {
                    val intent = Intent(activity, Quiz::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(270.dp)
                    .height(55.dp),
                colors = customButtonColors
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.puzzle),
                        contentDescription = "Quiz",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.weight(2f))
                    Text(
                        text = "Quiz",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f))
                }
            }
        }

        // Leaderboard button on the top left
        Button(
            onClick = {
                val intent = Intent(activity, Leaderboard::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart),
            colors = customButtonColors,
            shape = CircleShape,
            contentPadding = PaddingValues(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.leaderboard_icon),
                contentDescription = "Leaderboard",
                modifier = Modifier.size(50.dp)
            )
        }

        Button(
            onClick = {
                val intent = Intent(activity, UserProfile::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            colors = customButtonColors,
            shape = CircleShape,
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier.size(50.dp),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    val activity = AppCompatActivity()
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            elevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Replace 'R.drawable.grass_image' with the actual resource ID of your grass image
                val pokerMatTexture: Painter = painterResource(id = R.drawable.perfect_green_grass)
                Menu(activity = activity, pokerMatTexture = pokerMatTexture)
            }
        }
    }
}