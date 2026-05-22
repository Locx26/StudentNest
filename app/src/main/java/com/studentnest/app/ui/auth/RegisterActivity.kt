package com.studentnest.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.studentnest.app.databinding.ActivityRegisterBinding
import com.studentnest.app.data.model.User
import androidx.room.Room
import com.studentnest.app.data.database.AppDatabase
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "studentnest_database"
        ).fallbackToDestructiveMigration().build()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            if (fullName.isEmpty()) {
                binding.tilFullName.error = "Full name is required"
                isValid = false
            } else {
                binding.tilFullName.error = null
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Enter a valid email"
                isValid = false
            } else {
                binding.tilEmail.error = null
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                binding.tilPassword.error = "Password must be at least 6 characters"
                isValid = false
            } else {
                binding.tilPassword.error = null
            }

            if (isValid) {
                performRegister(fullName, email, password)
            }
        }
    }

    private fun performRegister(fullName: String, email: String, password: String) {
        binding.btnRegister.isEnabled = false
        binding.btnRegister.text = "Creating Account..."

        lifecycleScope.launch {
            try {
                val existingUser = database.userDao().getUserByEmail(email)
                if (existingUser != null) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email already registered",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Register"
                    return@launch
                }

                val newUser = User(
                    fullName = fullName,
                    email = email,
                    password = password
                )
                database.userDao().insertUser(newUser)

                Toast.makeText(
                    this@RegisterActivity,
                    "Welcome to StudentNest!",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.btnRegister.isEnabled = true
                binding.btnRegister.text = "Register"
            }
        }
    }
}
