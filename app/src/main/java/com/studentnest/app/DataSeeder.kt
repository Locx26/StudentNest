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
     * Requirement B: Generates 50 different types of house records with relevant house images
     */
    fun get50Listings(): List<Listing> {
        val gaboroneAreas = listOf("Block 6", "Phase 2", "Tlokweng", "Broadhurst", "Main Mall", "Village", "Mogoditshane", "Kgale")
        val houseTypes = listOf("Bachelor Pad", "1-Bedroom Flat", "Shared House", "Studio Apartment", "En-suite Room")
        val random = Random()

        // High-quality house images provided by the user
        val houseImages = listOf(
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=400&h=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=400&h=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1493809842364-78817add7ffb?q=80&w=400&h=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1560448204-603b3fc33ddc?q=80&w=400&h=300&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=400&h=300&auto=format&fit=crop"
        )

        return (1..50).map { i ->
            val houseType = houseTypes[i % houseTypes.size]

            Listing(
                id = 0, // Room auto-generates IDs when set to 0
                title = "$houseType #$i",
                type = houseType,
                // Requirement B: Price in BWP
                priceBWP = (1500..4500).randomValue().toDouble(),
                location = gaboroneAreas.random(),
                amenities = "WiFi, Laundry, Parking, Security",
                depositAmount = 1000.0,
                // Requirement C: Availability Date as Long (Today + i days)
                availabilityDate = System.currentTimeMillis() + (i.toLong() * 86400000L),
                // Requirement D: Default reservation status
                isReserved = false,
                reservationRef = null,
                // Cycles through the 5 high-quality images for the 50 records
                imageUrl = houseImages[i % houseImages.size],
                // Requirement E: Gaborone Coordinates for Campus Distance
                latitude = -24.658 + (random.nextDouble() - 0.5) / 10,
                longitude = 25.912 + (random.nextDouble() - 0.5) / 10
            )
        }
    }

    private fun IntRange.randomValue(): Int {
        return if (this.isEmpty()) 0 else Random().nextInt((last - first) + 1) + first
    }
}