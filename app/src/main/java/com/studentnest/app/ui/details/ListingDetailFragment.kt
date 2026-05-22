package com.studentnest.app.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.studentnest.app.R
import com.studentnest.app.databinding.ActivityListingDetailBinding
import com.studentnest.app.data.model.Reservation
import com.studentnest.app.ui.maps.MapsActivity
import com.studentnest.app.ui.reservation.ReservationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.room.Room
import com.studentnest.app.data.database.AppDatabase

@AndroidEntryPoint
class ListingDetailFragment : Fragment() {
    private lateinit var binding: ActivityListingDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityListingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listingId = arguments?.getInt("listingId", -1) ?: -1
        if (listingId != -1) {
            viewModel.getListingById(listingId)
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.listing.observe(viewLifecycleOwner) { listing ->
            if (listing != null) {
                binding.apply {
                    tvTitle.text = listing.title
                    tvLocation.text = listing.location
                    tvPrice.text = "BWP \u0050${String.format("%,.0f", listing.priceBWP)} / month"

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val availableDate = dateFormat.format(Date(listing.availabilityDate))
                    tvAvailabilityDate.text = "Available from: $availableDate"

                    tvAmenities.text = listing.amenities

                    tvDeposit.text = "Deposit: BWP \u0050${String.format("%,.0f", listing.depositAmount)}"

                    Glide.with(ivDetailImage.context)
                        .load(listing.imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .centerCrop()
                        .into(ivDetailImage)

                    if (listing.isReserved) {
                        tvReservedBadge.visibility = View.VISIBLE
                        btnPayDeposit.isEnabled = false
                        btnReserve.isEnabled = false
                        btnPayDeposit.text = "Already Reserved"
                        btnReserve.text = "Already Reserved"
                    } else {
                        tvReservedBadge.visibility = View.GONE
                        btnPayDeposit.isEnabled = true
                        btnReserve.isEnabled = true
                        btnPayDeposit.text = "Pay Deposit & Reserve"
                        btnReserve.text = "Reserve Now"
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnViewOnMap.setOnClickListener {
            val listing = viewModel.listing.value
            if (listing != null) {
                val intent = Intent(requireContext(), MapsActivity::class.java).apply {
                    putExtra("lat", listing.latitude)
                    putExtra("lng", listing.longitude)
                    putExtra("title", listing.title)
                }
                startActivity(intent)
            }
        }

        binding.btnPayDeposit.setOnClickListener {
            val listing = viewModel.listing.value
            if (listing != null && !listing.isReserved) {
                performReservation(listing)
            }
        }

        binding.btnReserve.setOnClickListener {
            val listing = viewModel.listing.value
            if (listing != null && !listing.isReserved) {
                performReservation(listing)
            }
        }
    }

    private fun performReservation(listing: com.studentnest.app.data.model.Listing) {
        MainScope().launch {
            try {
                val prefs = requireActivity().getSharedPreferences("studentnest_prefs", 0)
                val userId = prefs.getInt("userId", -1)

                if (userId == -1) {
                    Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val database = Room.databaseBuilder(
                    requireContext(),
                    AppDatabase::class.java,
                    "studentnest_database"
                ).fallbackToDestructiveMigration().build()

                val ref = "SN-${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}-${listing.id}"
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                database.listingDao().reserveListing(listing.id, ref)
                database.reservationDao().insertReservation(
                    Reservation(
                        userId = userId,
                        listingId = listing.id,
                        referenceNumber = ref,
                        paymentDate = today,
                        status = "Confirmed"
                    )
                )

                val intent = Intent(requireContext(), ReservationActivity::class.java).apply {
                    putExtra("ref", ref)
                    putExtra("title", listing.title)
                    putExtra("deposit", listing.depositAmount)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error during reservation: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
