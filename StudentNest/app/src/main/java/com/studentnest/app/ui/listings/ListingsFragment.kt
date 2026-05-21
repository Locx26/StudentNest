package com.studentnest.app.ui.listings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.studentnest.app.R
import com.studentnest.app.data.model.Listing
import com.studentnest.app.databinding.FragmentListingsBinding
import com.studentnest.app.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingsFragment : Fragment(R.layout.fragment_listings) {

    private var _binding: FragmentListingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListingsViewModel by viewModels()
    private lateinit var adapter: ListingsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListingsBinding.bind(view)

        setupRecyclerView()
        setupClickListeners()
        observeData()
        handleFilterResults()
    }

    /**
     * Requirement C: Smart Filtering logic
     * Receiving location, max price, and availability date from FilterFragment
     */
    private fun handleFilterResults() {
        val navBackStackEntry = findNavController().currentBackStackEntry
        navBackStackEntry?.savedStateHandle?.getLiveData<Bundle>("filter_results")
            ?.observe(viewLifecycleOwner) { bundle ->
                val location = bundle.getString("location") ?: ""
                val maxPrice = bundle.getDouble("max_price", 100000.0)

                // Requirement C: Filter by date
                val minDate = bundle.getLong("availability_date", 0L)

                viewModel.applyFilters(location, maxPrice, minDate)

                navBackStackEntry.savedStateHandle.remove<Bundle>("filter_results")
            }
    }

    private fun observeData() {
        viewModel.listings.observe(viewLifecycleOwner) { listings ->
            updateUI(listings)

            // Requirement C: Trigger Local Notification if a match is found
            if (listings.isNotEmpty()) {
                NotificationHelper.showListingMatchNotification(
                    requireContext(),
                    listings[0].title,
                    listings[0].location
                )
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Requirement D: Observe Reservation/Payment Status
        viewModel.reservationEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearReservationEvent()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ListingsAdapter(emptyList()) { listing ->
            // Requirement D: Logic to prevent reserving a reserved room
            if (listing.isReserved) {
                Toast.makeText(requireContext(), "This room is already reserved!", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to detail for payment/reservation
                val bundle = bundleOf("listingId" to listing.id)
                findNavController().navigate(
                    R.id.action_listingsFragment_to_detailFragment,
                    bundle
                )
            }
        }

        binding.rvListings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListingsFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.fabFilter.setOnClickListener {
            findNavController().navigate(R.id.action_listingsFragment_to_filterFragment)
        }

        binding.btnClearFilter.setOnClickListener {
            viewModel.loadAllListings()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerView.apply {
            if (isLoading) {
                startShimmer()
                visibility = View.VISIBLE
                binding.rvListings.visibility = View.GONE
            } else {
                stopShimmer()
                visibility = View.GONE
            }
        }
    }

    private fun updateUI(listings: List<Listing>) {
        val isEmpty = listings.isEmpty()
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvListings.visibility = if (isEmpty) View.GONE else View.VISIBLE

        if (!isEmpty) {
            adapter.updateData(listings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}