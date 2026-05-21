package com.studentnest.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Assignment Requirement B: House Listings (BWP, Gaborone Areas, Amenities, Deposit)
 * Assignment Requirement C: Smart Filtering (Availability Date)
 * Assignment Requirement D: Reservation Logic (isReserved, reservationRef)
 * Assignment Requirement E: Extension Feature (Lat/Lng for Campus Distance)
 */
@Parcelize
@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    // ADDED: Type field (e.g., "Bachelor Pad", "1-Bedroom")
    // This fixes the "Unresolved reference: type" error in your DataSeeder
    val type: String,

    // Requirement B: Price must be in BWP
    val priceBWP: Double,

    // Requirement B: Specific Gaborone locations
    val location: String,

    // Requirement B: Amenities (WiFi, Parking, etc.)
    val amenities: String,

    // Requirement B: Deposit amount record
    val depositAmount: Double,

    // Requirement C: Filter by availability date (Stored as timestamp Long)
    val availabilityDate: Long,

    // Requirement D: Change listing status to 'Reserved' after payment
    var isReserved: Boolean = false,

    // Requirement D: Generate receipt/reference number
    var reservationRef: String? = null,

    // Requirement B: Image per listing
    val imageUrl: String? = null,

    // Requirement E: Coordinates for Campus distance
    val latitude: Double = -24.658,
    val longitude: Double = 25.912
) : Parcelable