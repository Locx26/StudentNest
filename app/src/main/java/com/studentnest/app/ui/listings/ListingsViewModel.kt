package com.studentnest.app.ui.listings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentnest.app.data.model.Listing
import com.studentnest.app.DataSeeder
import com.studentnest.app.data.repository.ListingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ListingsViewModel @Inject constructor(
    private val repository: ListingRepository
) : ViewModel() {

    // 1. Requirement B: Primary list of 50 listings
    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> = _listings

    // 2. Loading state for Shimmer/ProgressBar
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 3. Requirement D: Reservation feedback (Success/Error messages)
    private val _reservationEvent = MutableLiveData<String?>()
    val reservationEvent: LiveData<String?> = _reservationEvent

    // Keeps a copy of the full list to allow resetting filters
    private var fullList: List<Listing> = emptyList()

    init {
        loadAllListings()
    }

    /**
     * Requirement B: Loads the 50 mandatory records.
     * Logic: Checks the Room database first. If empty, seeds from DataSeeder.
     */
    fun loadAllListings() {
        viewModelScope.launch {
            _isLoading.value = true

            // Try to fetch from database
            var data = repository.filterListings("", 100000.0, 0L)

            if (data.isEmpty()) {
                // Seed data if DB is empty
                val seedData = DataSeeder.get50Listings()
                // You should have an insertAll method in your repository/DAO
                // repository.insertAll(seedData)
                data = seedData
            }

            fullList = data
            _listings.value = data
            _isLoading.value = false
        }
    }

    /**
     * Requirement C: Smart Filtering Logic
     * Filters by Location, Price (BWP), and Availability Date.
     */
    fun applyFilters(location: String, maxPrice: Double, minDate: Long) {
        _isLoading.value = true

        val filteredList = fullList.filter { listing ->
            // Check location match
            val matchesLocation = location.isEmpty() ||
                    listing.location.contains(location, ignoreCase = true)

            // Check price match (Requirement B uses BWP)
            val matchesPrice = listing.priceBWP <= maxPrice

            // Requirement C: Filter by availability date
            val matchesDate = listing.availabilityDate >= minDate

            // Requirement D: Only show rooms that are not currently reserved
            matchesLocation && matchesPrice && matchesDate && !listing.isReserved
        }

        _listings.value = filteredList
        _isLoading.value = false
    }

    /**
     * Requirement D: Deposit and Reservation Logic
     */
    fun reserveListing(listing: Listing) {
        if (listing.isReserved) {
            _reservationEvent.value = "Error: This room is already reserved."
            return
        }

        viewModelScope.launch {
            // Requirement D: Generate receipt/reference number (e.g., SN-GAB-1234)
            val refNumber = "SN-GAB-${Random.nextInt(1000, 9999)}"

            // Update in Repository (Persistence)
            repository.reserveListing(listing.id, refNumber)

            // Update local UI state
            listing.isReserved = true
            listing.reservationRef = refNumber
            _listings.value = _listings.value?.map {
                if (it.id == listing.id) listing else it
            }

            _reservationEvent.value = "Success! Reserved with Ref: $refNumber"
        }
    }

    fun clearReservationEvent() {
        _reservationEvent.value = null
    }
}