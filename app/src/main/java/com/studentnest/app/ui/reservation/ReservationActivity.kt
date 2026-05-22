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
        binding = ActivityReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ref = intent.getStringExtra("ref") ?: "N/A"
        val title = intent.getStringExtra("title") ?: "Property"
        val deposit = intent.getDoubleExtra("deposit", 0.0)

        binding.apply {
            tvPropertyName.text = title
            tvReferenceNumber.text = ref
            tvDepositAmount.text = "BWP \u0050${String.format("%,.2f", deposit)}"

            btnDone.setOnClickListener {
                val intent = Intent(this@ReservationActivity, ListingsActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }
}
