package com.studentnest.app.ui.filter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.studentnest.app.R
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.databinding.ActivityFilterBinding
import com.studentnest.app.ui.listings.ListingsActivity
import kotlinx.coroutines.launch

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private val CHANNEL_ID = "studentnest_alerts"
    private val locations = listOf("All Areas", "Village", "Broadhurst", "Gaborone West", "Mogoditshane", "Block 8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        // Setup Location Spinner with professional dropdown style
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locations)
        binding.spinnerLocation.adapter = adapter

        // 1. APPLY FILTER LOGIC
        binding.btnApplyFilter.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

            val minP = binding.etMinPrice.text.toString().toDoubleOrNull() ?: 0.0
            val maxP = binding.etMaxPrice.text.toString().toDoubleOrNull() ?: 50000.0 // Default high if empty
            val loc = if (binding.spinnerLocation.selectedItemPosition == 0) "" else locations[binding.spinnerLocation.selectedItemPosition]
            val date = binding.etDate.text.toString().trim()

            // Pass data back to ListingsActivity via Intent extras
            val intent = Intent(this, ListingsActivity::class.java).apply {
                putExtra("apply_filter", true)
                putExtra("min_price", minP)
                putExtra("max_price", maxP)
                putExtra("location", loc)
                putExtra("date", date)
                // Clear the activity stack so "Back" doesn't return to old filter
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        // 2. SAVE PREFERENCES LOGIC
        binding.btnSavePreferences.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

            val prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)
            val selectedLoc = if (binding.spinnerLocation.selectedItemPosition == 0) "" else locations[binding.spinnerLocation.selectedItemPosition]
            val maxPrice = binding.etMaxPrice.text.toString().toFloatOrNull() ?: 0f

            prefs.edit()
                .putString("pref_location", selectedLoc)
                .putFloat("pref_maxPrice", maxPrice)
                .apply()

            sendMatchNotification()
            Toast.makeText(this, "Preferences saved! We'll alert you of matches.", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendMatchNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this exists!
            .setContentTitle("StudentNest Alert")
            .setContentText("New listings matching your preferences are available!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismisses notification when clicked
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "StudentNest Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for property matching alerts"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}