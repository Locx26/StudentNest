package com.studentnest.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "listings")
@Parcelize
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String,
    val priceBWP: Double,
    val location: String,
    val amenities: String,
    val depositAmount: Double,
    val availabilityDate: Long,
    var isReserved: Boolean = false,
    var reservationRef: String? = null,
    val imageUrl: String? = null,
    val latitude: Double = -24.658,
    val longitude: Double = 25.912
) : Parcelable
