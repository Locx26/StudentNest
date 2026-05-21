package com.studentnest.app.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.studentnest.app.R
import com.studentnest.app.databinding.ActivityListingDetailBinding // Reusing your existing layout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingDetailFragment : Fragment(R.layout.activity_listing_detail) {

    private var _binding: ActivityListingDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityListingDetailBinding.bind(view)

        // 1. Get the ID passed from ListingsFragment
        val listingId = arguments?.getInt("listingId") ?: -1

        if (listingId != -1) {
            loadListingDetails(listingId)
        }

        setupClickListeners()
    }

    private fun loadListingDetails(id: Int) {
        // Here you will eventually use a DetailViewModel to fetch data
        // For now, you can keep your existing display logic
    }


    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            // Use the NavController to go back instead of finish()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}