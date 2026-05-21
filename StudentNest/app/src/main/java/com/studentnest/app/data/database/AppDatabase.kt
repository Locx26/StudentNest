package com.studentnest.app.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.studentnest.app.data.dao.UserDao
import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.dao.ReservationDao
import com.studentnest.app.data.model.User
import com.studentnest.app.data.model.Listing
import com.studentnest.app.data.model.Reservation

/**
 * Requirement B, C, and D: Main Database Configuration
 * Version 7 handles the new Listing schema (priceBWP, type, availabilityDate).
 */
@Database(
    entities = [User::class, Listing::class, Reservation::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studentnest_db"
                )
                    // Requirement B & C: Migration fix
                    // This wipes the old database structure so the new 50 records can be seeded
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("AppDatabase", "Database created for the first time.")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
// THE STRAY RETURN BLOCK WAS REMOVED FROM HERE
