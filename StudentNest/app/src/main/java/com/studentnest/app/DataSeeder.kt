package com.studentnest.app

import android.content.Context
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random

object DataSeeder {

    /**
     * Requirement B: Seeds the database with 50 mandatory records if empty.
     */
    suspend fun seedIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            val database = AppDatabase.getInstance(context)
            val dao = database.listingDao()

            // Only seed if the database is currently empty
            if (dao.getListingCount() == 0) {
                val listings = get50Listings()
                dao.insertAll(listings)
            }
        }
    }

    /**
     * Requirement B: Generates 50 different types of house records
     */
    fun get50Listings(): List<Listing> {
        val gaboroneAreas = listOf("Block 6", "Phase 2", "Tlokweng", "Broadhurst", "Main Mall", "Village", "Mogoditshane", "Kgale")
        val houseTypes = listOf("Bachelor Pad", "1-Bedroom Flat", "Shared House", "Studio Apartment", "En-suite Room")
        val random = Random()

        return (1..50).map { i ->
            Listing(
                id = 0, // Room auto-generates IDs when set to 0
                title = "${houseTypes.random()} #$i",
                type = houseTypes.random(), // Added to match your Listing model
                priceBWP = (1500..4500).randomValue().toDouble(),
                location = gaboroneAreas.random(),
                amenities = "WiFi, Laundry, Parking",
                depositAmount = 1000.0,
                // Requirement C: Availability Date as Long
                availabilityDate = System.currentTimeMillis() + (i.toLong() * 86400000L),
                isReserved = false,
                reservationRef = null,
                imageUrl = "https://picsum.photos/seed/${i + 100}/400/300",
                // Requirement E: Coordinates for Gaborone
                latitude = -24.658 + (random.nextDouble() - 0.5) / 10,
                longitude = 25.912 + (random.nextDouble() - 0.5) / 10
            )
        }
    }

    // Fixed helper function name to avoid conflict and handled potential empty range
    private fun IntRange.randomValue(): Int {
        return if (this.isEmpty()) 0 else Random().nextInt((last - first) + 1) + first
    }
}