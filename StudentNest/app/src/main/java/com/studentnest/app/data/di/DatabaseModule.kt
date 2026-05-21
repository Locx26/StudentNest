package com.studentnest.app.data.di

import android.content.Context
import com.studentnest.app.data.dao.ListingDao
import com.studentnest.app.data.dao.UserDao
import com.studentnest.app.data.dao.ReservationDao
import com.studentnest.app.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Use the singleton instance from AppDatabase
        // AppDatabase already contains the .fallbackToDestructiveMigration() logic
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideListingDao(db: AppDatabase): ListingDao {
        return db.listingDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun provideReservationDao(db: AppDatabase): ReservationDao {
        return db.reservationDao()
    }
}