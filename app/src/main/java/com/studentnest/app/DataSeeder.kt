package com.studentnest.app

import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.model.Listing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DataSeeder {
    fun seedIfEmpty(database: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val count = database.listingDao().getListingCount()
            if (count == 0) {
                val listings = generateListings()
                database.listingDao().insertAllListings(listings)
            }
        }
    }

    private fun generateListings(): List<Listing> {
        val areas = listOf(
            "Block 6", "Phase 2", "Tlokweng", "Broadhurst",
            "Main Mall", "Village", "Mogoditshane", "Kgale"
        )
        val types = listOf(
            "Bachelor Pad", "1-Bedroom Flat", "Shared House",
            "Studio Apartment", "En-suite Room"
        )

        val listings = mutableListOf<Listing>()
        val baseLatitude = -24.658
        val baseLongitude = 25.912

        for (i in 0 until 50) {
            val area = areas[i % areas.size]
            val type = types[i % types.size]
            val price = 1500.0 + (i * 70)
            val imageUrl = "https://picsum.photos/seed/${i + 100}/400/300"
            val latitude = baseLatitude + (((i % 10) - 5) * 0.005)
            val longitude = baseLongitude + (((i / 10) - 2) * 0.005)

            listings.add(
                Listing(
                    id = 0,
                    title = "$type - $area #${i + 1}",
                    type = type,
                    priceBWP = price,
                    location = area,
                    amenities = generateAmenities(type),
                    depositAmount = price * 2,
                    availabilityDate = System.currentTimeMillis() + (i * 86400000L),
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude
                )
            )
        }

        return listings
    }

    private fun generateAmenities(type: String): String {
        return when (type) {
            "Bachelor Pad" -> "Furnished, WiFi, Utilities Included, Laundry"
            "1-Bedroom Flat" -> "Furnished, WiFi, Security, Parking"
            "Shared House" -> "Common Area, WiFi, Garden, Utilities"
            "Studio Apartment" -> "Kitchenette, WiFi, Balcony, Furnished"
            "En-suite Room" -> "Private Bathroom, WiFi, Shared Kitchen"
            else -> "WiFi, Furnished"
        }
    }
}
