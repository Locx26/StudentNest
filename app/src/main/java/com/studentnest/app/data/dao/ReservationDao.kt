package com.studentnest.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studentnest.app.data.model.Reservation

@Dao
interface ReservationDao {
    @Insert
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: Int): Reservation?

    @Query("SELECT * FROM reservations WHERE userId = :userId")
    suspend fun getReservationsByUserId(userId: Int): List<Reservation>

    @Query("SELECT * FROM reservations WHERE referenceNumber = :ref")
    suspend fun getReservationByRef(ref: String): Reservation?
}
