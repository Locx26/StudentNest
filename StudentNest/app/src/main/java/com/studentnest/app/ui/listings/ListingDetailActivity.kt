package com.studentnest.app.ui.listings

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.studentnest.app.R
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.model.Reservation
import com.studentnest.app.databinding.ActivityListingDetailBinding
import com.studentnest.app.ui.maps.MapsActivity
import com.studentnest.app.ui.reservation.ReservationActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ListingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listingId = intent.getIntExtra("listingId", -1)
        val prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val db = AppDatabase.getInstance(this)

        lifecycleScope.launch {
            // Fetch listing from DB
            val listing = db.listingDao().getListingById(listingId) ?: return@launch

            // 1. Image Loading (Glide)
            Glide.with(this@ListingDetailActivity)
                .load(listing.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(binding.ivDetailImage)

            // 2. Data Population
            binding.tvDetailTitle.text = listing.title
            binding.tvDetailLocation.text = listing.location

            // FIX: Changed price to priceBWP (Requirement B)
            binding.tvDetailPrice.text = "BWP ${listing.priceBWP}"

            binding.tvDetailAmenities.text = "Amenities: ${listing.amenities}"
            binding.tvDetailDeposit.text = "Deposit Required: BWP ${listing.depositAmount}"

            // FIX: Format Long timestamp to readable Date (Requirement C)
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateString = sdf.format(Date(listing.availabilityDate))
            binding.tvDetailDate.text = "Available From: $dateString"

            if (listing.isReserved) {
                binding.btnPayDeposit.text = "Already Reserved"
                binding.btnPayDeposit.isEnabled = false
                binding.btnReserve.isEnabled = false
            }

            // 3. Payment/Reservation Logic
            binding.btnPayDeposit.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                if (userId == -1) {
                    Toast.makeText(this@ListingDetailActivity, "Please login first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                processReservation(db, listing, userId)
            }

            binding.btnReserve.setOnClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                if (userId == -1) {
                    Toast.makeText(this@ListingDetailActivity, "Please login first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                processReservation(db, listing, userId)
            }

            // 4. Map Logic (Requirement E)
            binding.btnViewOnMap.setOnClickListener {
                val mapIntent = Intent(this@ListingDetailActivity, MapsActivity::class.java)
                mapIntent.putExtra("lat", listing.latitude)
                mapIntent.putExtra("lng", listing.longitude)
                mapIntent.putExtra("title", listing.title)
                startActivity(mapIntent)
            }
        }
    }

    private fun processReservation(db: AppDatabase, listing: com.studentnest.app.data.model.Listing, userId: Int) {
        lifecycleScope.launch {
            try {
                // FIX: Generate Reference Number BEFORE calling DAO (Requirement D)
                val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                val ref = "SN-$dateStr-${listing.id}"

                // FIX: Pass ref String instead of userId Int to match DAO signature
                db.listingDao().reserveListing(listing.id, ref)

                // Create Reservation Record
                val reservation = Reservation(
                    userId = userId,
                    listingId = listing.id,
                    referenceNumber = ref,
                    paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                db.reservationDao().insertReservation(reservation)

                // Navigate to Success Activity
                val successIntent = Intent(this@ListingDetailActivity, ReservationActivity::class.java)
                successIntent.putExtra("ref", ref)
                successIntent.putExtra("title", listing.title)
                successIntent.putExtra("deposit", listing.depositAmount)
                startActivity(successIntent)

                finish()
            } catch (e: Exception) {
                Toast.makeText(this@ListingDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}