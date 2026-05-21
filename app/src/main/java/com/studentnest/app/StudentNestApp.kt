package com.studentnest.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Technical Requirement: Hilt Application class
 * Requirement B: Triggers the data seeding of 50 records on startup
 */
@HiltAndroidApp
class StudentNestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Seed the database with 50 listings if it is empty.
        // We use Dispatchers.IO because database operations must not block the Main thread.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DataSeeder.seedIfEmpty(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}