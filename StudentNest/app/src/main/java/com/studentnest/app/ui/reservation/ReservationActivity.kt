package com.studentnest.app.ui.reservation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studentnest.app.databinding.ActivityReservationBinding
import com.studentnest.app.ui.listings.ListingsActivity

class ReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data passed from ListingDetailActivity
        val ref = intent.getStringExtra("ref") ?: "N/A"
        val title = intent.getStringExtra("title") ?: "Unknown Property"
        val deposit = intent.getDoubleExtra("deposit", 0.0)

        // Update the UI components
        binding.tvReceiptTitle.text = "Property: $title"
        binding.tvReceiptRef.text = "Reference: $ref"
        binding.tvReceiptAmount.text = "Deposit Paid: BWP %.2f".format(deposit)

        // Button to return to the main listings screen
        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, ListingsActivity::class.java)
            startActivity(intent)
            // finishAffinity() closes all previous activities so the user
            // doesn't "go back" into the payment screen.
            finishAffinity()
        }
    }
}
