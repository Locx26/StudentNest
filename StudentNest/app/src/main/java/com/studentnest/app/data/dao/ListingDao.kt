package com.studentnest.app.data.dao

import androidx.room.*
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {

    @Query("SELECT * FROM listings ORDER BY id DESC")
    fun getAllListingsAsFlow(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE isReserved = 0")
    fun getAllAvailableListings(): Flow<List<Listing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listings: List<Listing>)

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingById(id: Int): Listing?

    @Query("SELECT * FROM listings")
    suspend fun getAllListingsOnce(): List<Listing>

    /**
     * Requirement C: Smart Filtering logic
     * Fixed to use 'priceBWP' and added 'availabilityDate' filter
     */
    @Query("""
        SELECT * FROM listings 
        WHERE (:location = '' OR location LIKE '%' || :location || '%') 
        AND (priceBWP <= :maxPrice)
        AND (availabilityDate >= :minDate)
        AND (isReserved = 0)
    """)
    suspend fun getListingsByFilter(location: String, maxPrice: Double, minDate: Long): List<Listing>

    /**
     * Requirement D: Reservation logic
     * Updates status and saves the reference number/receipt
     */
    @Query("UPDATE listings SET isReserved = 1, reservationRef = :ref WHERE id = :listingId")
    suspend fun reserveListing(listingId: Int, ref: String)

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getListingCount(): Int

    @Delete
    suspend fun delete(listing: Listing)
}