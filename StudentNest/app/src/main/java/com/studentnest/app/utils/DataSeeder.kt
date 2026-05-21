package com.studentnest.app.utils

import com.studentnest.app.data.model.Listing
import java.util.Random

object DataSeeder {
    fun get50Listings(): List<Listing> {
        val areas = listOf("Block 6", "Phase 2", "Tlokweng", "Broadhurst", "Main Mall", "Village")
        val amenitiesList = listOf("WiFi, Parking", "En-suite, WiFi", "Kitchen, Security", "WiFi, Near Bus Stop")
        val random = Random()

        return (1..50).map { i ->
            Listing(
                id = i,
                title = "Student Room #$i",
                // Requirement B: Price in BWP
                priceBWP = (1500..4500).randomValue().toDouble(),
                location = areas.random(),
                type = "Student Accommodation", // Added this to match fixed Listing.kt
                amenities = amenitiesList.random(),
                depositAmount = 1000.0,
                // Requirement C: Availability Date as Long (Timestamp)
                availabilityDate = System.currentTimeMillis() + (i.toLong() * 86400000L),
                isReserved = false,
                reservationRef = null,
                imageUrl = "https://picsum.photos/seed/$i/400/300",
                // Requirement E: Gaborone Coordinates
                latitude = -24.658 + (random.nextDouble() - 0.5) / 10,
                longitude = 25.912 + (random.nextDouble() - 0.5) / 10
            )
        }
    }

    /**
     * UPDATED HELPER: Uses a more reliable calculation for ranges.
     * Ensures the upper bound is inclusive.
     */
    private fun IntRange.randomValue(): Int {
        if (this.isEmpty()) return 0
        // java.util.Random().nextInt(n) returns 0..n-1
        // We add 1 to endInclusive - start to make the range inclusive
        return Random().nextInt(last - first + 1) + first
    }
}