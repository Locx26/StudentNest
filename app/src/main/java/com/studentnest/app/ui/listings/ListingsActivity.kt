package com.studentnest.app.ui.listings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studentnest.app.databinding.ActivityListingsBinding
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.DataSeeder
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "studentnest_database"
        ).fallbackToDestructiveMigration().build()

        DataSeeder.seedIfEmpty(database)
    }
}
