package com.studentnest.app.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.studentnest.app.data.database.AppDatabase
import com.studentnest.app.databinding.ActivityLoginBinding
import com.studentnest.app.ui.listings.ListingsActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("studentnest_prefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AppDatabase.getInstance(this)

            // Using lifecycleScope for database operations
            lifecycleScope.launch {
                val user = db.userDao().login(email, pass)

                // Room suspend functions return to the Main thread automatically
                // when finished, so runOnUiThread is not strictly necessary here.
                if (user != null) {
                    prefs.edit()
                        .putInt("userId", user.id)
                        .putString("userName", user.fullName)
                        .apply()

                    val intent = Intent(this@LoginActivity, ListingsActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}