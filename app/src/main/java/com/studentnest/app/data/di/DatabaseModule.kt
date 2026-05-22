package com.studentnest.app.data.di

import android.content.Context
import androidx.room.Room
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.dao.ReservationDao
import com.studentnest.app.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "studentnest_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideListingDao(database: AppDatabase): ListingDao {
        return database.listingDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideReservationDao(database: AppDatabase): ReservationDao {
        return database.reservationDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabaseInstance(database: AppDatabase): AppDatabase {
        return database
    }
}
