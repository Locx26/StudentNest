package com.studentnest.app.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.studentnest.app.databinding.ActivityLoginBinding
import com.studentnest.app.ui.listings.ListingsActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and SharedPreferences
        auth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform Firebase Login
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser

                        // FIX: Save user session to SharedPreferences
                        // This fixes the "userId = -1" error in ListingDetailActivity
                        prefs.edit()
                            .putInt("userId", 1) // Using '1' as a default ID for the logged-in student
                            .putString("userEmail", firebaseUser?.email)
                            .apply()

                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, ListingsActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle Firebase Errors (e.g. Configuration Not Found)
                        val errorMsg = task.exception?.message ?: "Authentication failed"
                        Toast.makeText(this@LoginActivity, "Login Failed: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}