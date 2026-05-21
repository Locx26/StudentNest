package com.studentnest.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Link to Firebase Authentication UID
    val uid: String = "",

    // Added default empty strings so Firebase can read/write this class
    val fullName: String = "",
    val email: String = "",
    val password: String = "",

    // Requirement A-A: Role-based access (Defaults to Student)
    val role: String = "Student"
)
