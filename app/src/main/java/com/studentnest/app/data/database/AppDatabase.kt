package com.studentnest.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.dao.ReservationDao
import com.studentnest.app.data.dao.UserDao
import com.studentnest.app.data.model.Listing
import com.studentnest.app.data.model.Reservation
import com.studentnest.app.data.model.User

@Database(entities = [Listing::class, User::class, Reservation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao
    abstract fun userDao(): UserDao
    abstract fun reservationDao(): ReservationDao
}
