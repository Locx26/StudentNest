package com.studentnest.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.model.User
import com.studentnest.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Register Button Click
        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        // Navigate to Login Link
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegistration() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        // 1. Professional Validation (Inline errors instead of just Toasts)
        if (!validateInputs(name, email, pass)) return

        // 2. Database Instance
        val db = AppDatabase.getInstance(this)

        // 3. UI Feedback: Start Loading State
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val existing = db.userDao().getUserByEmail(email)

                if (existing != null) {
                    setLoadingState(false)
                    // Professional UI: Show error on the specific field
                    binding.etEmail.error = "Email already registered"
                    Toast.makeText(this@RegisterActivity, "Account already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new user
                    db.userDao().insertUser(User(fullName = name, email = email, password = pass))

                    Toast.makeText(this@RegisterActivity, "Welcome to StudentNest!", Toast.LENGTH_LONG).show()

                    // Navigate to Login
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                setLoadingState(false)
                Toast.makeText(this@RegisterActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(name: String, email: String, pass: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etFullName.error = "Name is required"
            isValid = false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Enter a valid email"
            isValid = false
        }

        if (pass.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Prevents duplicate clicks and gives visual feedback
     */
    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Creating Account..."
            binding.btnRegister.alpha = 0.7f // Makes it look visually "disabled"
        } else {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "Register"
            binding.btnRegister.alpha = 1.0f
        }
    }
}