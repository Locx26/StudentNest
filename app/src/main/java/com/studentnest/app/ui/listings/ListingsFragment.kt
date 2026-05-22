package com.studentnest.app.ui.listings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.studentnest.app.R
import com.studentnest.app.databinding.FragmentListingsBinding
import com.studentnest.app.data.model.Listing
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingsFragment : Fragment() {
    private lateinit var binding: FragmentListingsBinding
    private val viewModel: ListingsViewModel by viewModels()
    private lateinit var adapter: ListingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupObservers()
        setupClickListeners()
        handleFilterResults()
    }

    private fun setupAdapter() {
        adapter = ListingsAdapter(onClick = { listing ->
            navigateToDetail(listing)
        })
        binding.rvListings.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.listings.observe(viewLifecycleOwner) { listings ->
            adapter.submitList(listings)
            if (listings.isEmpty()) {
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.rvListings.visibility = View.GONE
            } else {
                binding.emptyStateContainer.visibility = View.GONE
                binding.rvListings.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerViewContainer.startShimmer()
                binding.shimmerViewContainer.visibility = View.VISIBLE
            } else {
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabFilter.setOnClickListener {
            findNavController().navigate(R.id.action_listingsFragment_to_filterFragment)
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.loadAllListings()
        }
    }

    private fun handleFilterResults() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>("filter_results")
            ?.observe(viewLifecycleOwner) { bundle ->
                val location = bundle.getString("location")
                val maxPrice = bundle.getDouble("max_price", Double.MAX_VALUE)
                val availabilityDate = bundle.getLong("availability_date", 0L)

                viewModel.applyFilters(
                    location = if (location == "All Areas") null else location,
                    maxPrice = if (maxPrice == Double.MAX_VALUE) null else maxPrice,
                    minDate = if (availabilityDate == 0L) null else availabilityDate
                )

                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>("filter_results")
            }
    }

    private fun navigateToDetail(listing: Listing) {
        val bundle = Bundle().apply {
            putInt("listingId", listing.id)
        }
        findNavController().navigate(R.id.action_listingsFragment_to_detailFragment, bundle)
    }
}
