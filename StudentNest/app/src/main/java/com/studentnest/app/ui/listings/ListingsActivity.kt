package com.studentnest.app.ui.listings

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.studentnest.app.databinding.ActivityListingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListingsBinding

    // Declare ViewModel using the ktx extension
    private val viewModel: ListingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Requirement B: Initial load of the 50 house records
        viewModel.loadAllListings()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Requirement C: Handling Filter Results
     * Receives Location, Max Price, and Date from the Filter screen.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            val location = data.getStringExtra("SELECTED_LOCATION") ?: ""
            val maxPrice = data.getDoubleExtra("MAX_PRICE", 100000.0)
            val minDate = data.getLongExtra("AVAILABILITY_DATE", 0L)

            // FIX: Changed setFilter to applyFilters to match your updated ViewModel
            viewModel.applyFilters(location, maxPrice, minDate)
        }
    }
}