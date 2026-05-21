package com.studentnest.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.data.model.User
import com.studentnest.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleRegistration() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString().trim()

        if (!validateInputs(name, email, pass)) return

        setLoadingState(true)

        // 1. Firebase Authentication for Role-Based Access
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""

                    // 2. Save to Firebase Realtime Database (Access Control Proof)
                    saveUserToFirebase(uid, name, email)

                    // 3. Save to Local Room Database
                    saveUserToRoom(name, email, pass)
                } else {
                    setLoadingState(false)
                    Toast.makeText(this, "Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirebase(uid: String, name: String, email: String) {
        val userMap = mapOf(
            "uid" to uid,
            "fullName" to name,
            "email" to email,
            "role" to "Student" // REQUIREMENT A-A: Role assignment
        )

        FirebaseDatabase.getInstance().getReference("Users")
            .child(uid)
            .setValue(userMap)
    }

    private fun saveUserToRoom(name: String, email: String, pass: String) {
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            try {
                // Room insertion with the 'Student' role
                db.userDao().insertUser(User(fullName = name, email = email, password = pass, role = "Student"))

                setLoadingState(false)
                Toast.makeText(this@RegisterActivity, "Student Account Created!", Toast.LENGTH_LONG).show()

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                setLoadingState(false)
                Toast.makeText(this@RegisterActivity, "Room Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(name: String, email: String, pass: String): Boolean {
        var isValid = true
        if (name.isEmpty()) { binding.etFullName.error = "Name required"; isValid = false }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Valid email required"; isValid = false
        }
        if (pass.length < 6) { binding.etPassword.error = "Min 6 characters"; isValid = false }
        return isValid
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.text = if (isLoading) "Creating Account..." else "Register"
        binding.btnRegister.alpha = if (isLoading) 0.7f else 1.0f
    }
}