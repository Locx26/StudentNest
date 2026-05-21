package com.studentnest.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.studentnest.app.R

/**
 * Requirement C: Smart Filtering & Alerts
 * This helper provides local notifications when a listing matches user preferences.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "student_nest_alerts"
    private const val CHANNEL_NAME = "Listing Alerts"
    private const val CHANNEL_DESC = "Notifications for matching house listings"

    /**
     * Initializes the Notification Channel.
     * Call this in your Application class or MainActivity.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Requirement C: Shows a local notification when a smart match is found.
     */
    fun showListingMatchNotification(context: Context, houseTitle: String, location: String) {
        // Check for POST_NOTIFICATIONS permission (Required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return // Permission not granted, cannot show notification
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home) // Ensure you have a vector drawable named ic_home
            .setContentTitle("Smart Match Found!")
            .setContentText("A room in $location matches your preference: $houseTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}