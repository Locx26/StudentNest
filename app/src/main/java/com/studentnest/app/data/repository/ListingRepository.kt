package com.studentnest.app.data.repository

import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingRepository @Inject constructor(
    private val listingDao: ListingDao
) {
    // Requirement B: Real-time flow of all house listings
    val allListings: Flow<List<Listing>> = listingDao.getAllListingsAsFlow()

    /**
     * Requirement C: Smart Filtering logic
     * @param location Gaborone area string
     * @param maxPrice Maximum price in BWP (Double)
     * @param minDate Minimum availability date (Long - Timestamp)
     */
    suspend fun filterListings(location: String, maxPrice: Double, minDate: Long): List<Listing> {
        // FIX: The parameters now match the DAO signature (String, Double, Long)
        return listingDao.getListingsByFilter(location, maxPrice, minDate)
    }

    /**
     * Requirement D: Used to fetch details for the reservation process
     */
    suspend fun getListingById(id: Int): Listing? {
        return listingDao.getListingById(id)
    }

    /**
     * Requirement D: Updates the database when a student reserves a room
     */
    suspend fun reserveListing(listingId: Int, reference: String) {
        listingDao.reserveListing(listingId, reference)
    }
}