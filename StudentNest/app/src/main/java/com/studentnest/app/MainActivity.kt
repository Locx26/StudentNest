package com.studentnest.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.studentnest.app.ui.auth.LoginActivity
import com.studentnest.app.ui.listings.ListingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply window insets to prevent status bar and navigation bar overlap
        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Splash Screen Timer (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)
            val userId = prefs.getInt("userId", -1)

            // If user is logged in, go to ListingsActivity. Otherwise, go to LoginActivity.
            val targetActivity = if (userId != -1) {
                ListingsActivity::class.java
            } else {
                LoginActivity::class.java
            }

            val intent = Intent(this, targetActivity)
            startActivity(intent)

            // Optional: Smooth transition between splash and next screen
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // Closes the splash screen so the user cannot navigate back to it
            finish()
        }, 2000)
    }
}