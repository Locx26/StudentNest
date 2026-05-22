package com.studentnest.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val listingId: Int,
    val referenceNumber: String,
    val paymentDate: String,
    val status: String = "Confirmed"
)
