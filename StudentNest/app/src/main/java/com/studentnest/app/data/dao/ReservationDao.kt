package com.studentnest.app.data.dao

import androidx.room.*
import com.studentnest.app.data.model.Reservation

@Dao
interface ReservationDao {

    @Insert
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations WHERE userId = :userId")
    suspend fun getReservationsByUser(userId: Int): List<Reservation>
}