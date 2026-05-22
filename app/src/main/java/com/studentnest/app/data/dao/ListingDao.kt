package com.studentnest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Insert
    suspend fun insertAllListings(listings: List<Listing>)

    @Query("SELECT * FROM listings")
    fun getAllListings(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getListingById(id: Int): Listing?

    @Query("SELECT * FROM listings WHERE location = :location")
    fun getListingsByLocation(location: String): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE priceBWP <= :maxPrice")
    fun getListingsByMaxPrice(maxPrice: Double): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE availabilityDate >= :minDate")
    fun getListingsByAvailabilityDate(minDate: Long): Flow<List<Listing>>

    @Query("SELECT COUNT(*) FROM listings")
    suspend fun getListingCount(): Int

    @Query("UPDATE listings SET isReserved = 1, reservationRef = :ref WHERE id = :id")
    suspend fun reserveListing(id: Int, ref: String)

    @Update
    suspend fun updateListing(listing: Listing)
}
