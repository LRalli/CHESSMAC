package com.example.chessmac.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.chessmac.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.UUID

class UserProfile : AppCompatActivity() {

    private lateinit var profileImage: ShapeableImageView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceHistory: DatabaseReference
    private lateinit var usernameTextView: TextView
    private lateinit var quizScoresTextView: TextView
    private lateinit var stockfishHistoryTextView: TextView
    private lateinit var logoutButton: Button
    private var currentUser: User? = null
    private lateinit var storageReference: StorageReference

    private val imagePickLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                try {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    userId?.let { uid ->
                        val imageRef = storageReference.child("profile_images/$uid")
                        // Check if user already has a profile picture
                        imageRef.listAll().addOnSuccessListener { result ->
                            // Delete old profile picture if it exists
                            result.items.forEach { oldImage ->
                                oldImage.delete()
                            }
                            // Upload new profile picture
                            val imageName = UUID.randomUUID().toString()
                            val newImageRef = storageReference.child("profile_images/$uid/$imageName")
                            val uploadTask = newImageRef.putFile(it)
                            uploadTask.addOnSuccessListener { _ ->
                                // If successful, update profile image URL in the database
                                newImageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                    // Update profile image URL in the database
                                    currentUser?.let { user ->
                                        user.profileImageUrl = imageUrl.toString()
                                        user.profileImageUrl?.let { url ->
                                            val profileUpdates = hashMapOf<String, Any>(
                                                "profileImageUrl" to url
                                            )
                                            databaseReference.updateChildren(profileUpdates)
                                        }
                                    }

                                    // Load the newly uploaded image and apply centerCrop transformation
                                    Glide.with(this@UserProfile)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.placeholder_image)
                                        .error(R.drawable.error_image)
                                        .centerCrop()
                                        .into(profileImage)
                                }
                            }.addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Hide the action bar
        supportActionBar?.hide()

        profileImage = findViewById(R.id.profile_image)
        usernameTextView = findViewById(R.id.tvNickname)
        quizScoresTextView = findViewById(R.id.tvQuizScores)
        stockfishHistoryTextView = findViewById(R.id.tvStockfishHistory)
        logoutButton = findViewById(R.id.btnLogout)

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://chessmacc-3aaab.appspot.com")

        retrieveUserProfile()

        profileImage.setOnClickListener {
            imagePickLauncher.launch("image/*")
        }

        logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun retrieveUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Retrieve user profile data from Firebase Realtime Database
            databaseReference = FirebaseDatabase.getInstance("https://chessmacc-3aaab-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("UsersScore/$userId")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.let {
                        currentUser = user
                        usernameTextView.text = user.nickname

                        // Load profile image if available
                        if (!user.profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this@UserProfile)
                                .load(user.profileImageUrl)
                                .placeholder(R.drawable.default_usr)
                                .error(R.drawable.error_image)
                                .centerCrop()
                                .into(profileImage)
                        } else {
                            // Set placeholder image if no profile image URL is found
                            profileImage.setImageResource(R.drawable.default_usr)
                        }

                        // Fetch and format all quiz scores along with dates
                        val quizScores = mutableListOf<String>()
                        dataSnapshot.children.forEach { scoreSnapshot ->
                            if (scoreSnapshot.key?.startsWith("scores") == true) {
                                scoreSnapshot.children.forEach { subScoreSnapshot ->
                                    if (subScoreSnapshot.child("score").exists()) {
                                        val scoreValue = subScoreSnapshot.child("score").getValue(Double::class.java)
                                        val dateValue = subScoreSnapshot.child("date").getValue(String::class.java)
                                        scoreValue?.let { score ->
                                            dateValue?.let { date ->
                                                quizScores.add("$date | $score")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val quizScoresText = quizScores.joinToString("\n")
                        quizScoresTextView.text = quizScoresText
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserProfile", "Failed to retrieve user profile: ${error.message}")
                }
            })

            // Retrieve user stock history data from Firebase Realtime Database
            databaseReferenceHistory = FirebaseDatabase.getInstance("https://chessmacc-3aaab-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("UsersHistory/$userId")
            databaseReferenceHistory.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val historyText = StringBuilder()
                    dataSnapshot.children.forEach { recordSnapshot ->
                        val record = recordSnapshot.getValue(String::class.java)
                        record?.let {
                            // Append each record to the historyText StringBuilder
                            val (result, difficulty, date) = it.split("/")
                            historyText.append("$date | $difficulty | $result\n")
                        }
                    }
                    stockfishHistoryTextView.text = historyText.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserProfile", "Failed to retrieve user profile: ${error.message}")
                }
            })

        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Registration::class.java)
        startActivity(intent)
        finish()
    }
}
