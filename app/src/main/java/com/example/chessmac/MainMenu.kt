package com.example.chessmac

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.example.chessmac.ui.theme.ChessMACTheme
import com.example.chessmac.utils.Leaderboard
import androidx.compose.ui.graphics.Brush
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
        backgroundColor = Color(0xFF3A5730) // Set the desired background color here
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo), // Replace 'app_logo' with your logo resource
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(bottom = 16.dp) // Add padding below the logo
                    .size(300.dp) // Adjust the size as needed
            )
            Button(
                onClick = {
                    val intent = Intent(activity, LocalGame::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier.padding(8.dp).width(270.dp).height(55.dp), // Set fixed width here
                colors = customButtonColors // Apply custom button colors here
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.handshake), // Replace 'local_game_image' with your image file name
                        contentDescription = "Local Game Image",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Flexible spacer to push text to the center
                    Text(
                        text = "Local Game",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f)) // Add another spacer after the text
                }
            }
            Button(
                onClick = {
                    val intent = Intent(activity, StockGame::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier.padding(8.dp).width(270.dp).height(55.dp), // Set fixed width here
                colors = customButtonColors // Apply custom button colors here
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.robot), // Replace 'robot' with your image file name
                        contentDescription = "StockFish",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Flexible spacer to push text to the center
                    Text(
                        text = "Stockfish Game",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f)) // Add another spacer after the text
                }
            }
            Button(
                onClick = {
                    val intent = Intent(activity, Quiz::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier.padding(8.dp).width(270.dp).height(55.dp), // Set fixed width here
                colors = customButtonColors // Apply custom button colors here
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.puzzle), // Replace 'robot' with your image file name
                        contentDescription = "Quiz",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.weight(2f)) // Flexible spacer to push text to the center
                    Text(
                        text = "Quiz",
                        color = Color.White,
                        style = TextStyle(fontSize = 20.sp, fontFamily = customFontFamily)
                    )
                    Spacer(modifier = Modifier.weight(2.5f)) // Add another spacer after the text
                }
            }
        }

        // Leaderboard button on the top left
        Button(
            onClick = {
                val intent = Intent(activity, Leaderboard::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp).align(Alignment.TopStart), // Set alignment here
            colors = customButtonColors, // Apply custom button colors here
            shape = CircleShape,
            contentPadding = PaddingValues(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.leaderboard_icon), // Replace 'your_image' with your image file name
                contentDescription = "Leaderboard",
                modifier = Modifier.size(50.dp)
            )
        }

        // Profile button on the top right
        Button(
            onClick = {
                val intent = Intent(activity, UserProfile::class.java)
                activity.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp).align(Alignment.TopEnd), // Set alignment here
            colors = customButtonColors, // Apply custom button colors here
            shape = CircleShape, // Set shape to circle
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
                val pokerMatTexture: Painter = rememberVectorPainter(image = Icons.Default.AccountCircle) // Placeholder image
                Menu(activity = activity, pokerMatTexture = pokerMatTexture)
            }
        }
    }
}