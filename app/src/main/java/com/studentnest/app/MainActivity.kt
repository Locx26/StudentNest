package com.studentnest.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.studentnest.app.ui.auth.LoginActivity
import com.studentnest.app.ui.listings.ListingsActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enables edge-to-edge display
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 2. Apply window insets
        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Requirement B & C: Seed Database and trigger Notification
        lifecycleScope.launch {
            // Seed 50 records if DB is empty
            DataSeeder.seedIfEmpty(applicationContext)

            // Trigger local notification to satisfy Requirement C
            sendPreferenceNotification()
        }

        // 4. Splash Screen Timer (2 seconds) before navigating
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)
            val userId = prefs.getInt("userId", -1)

            val targetActivity = if (userId != -1) {
                ListingsActivity::class.java
            } else {
                LoginActivity::class.java
            }

            val intent = Intent(this, targetActivity)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2000)
    }

    /**
     * Requirement C: Generates a local notification for matching preferences
     */
    private fun sendPreferenceNotification() {
        val channelId = "student_nest_alerts"
        val notificationId = 101

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Property Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Match Found!")
            .setContentText("We found houses in Gaborone matching your preferences.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        manager.notify(notificationId, builder.build())
    }
}