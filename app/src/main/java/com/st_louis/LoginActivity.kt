package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.st_louis.databinding.ActivityLoginBinding
import com.st_louis.data.repository.AuthRepository
import com.st_louis.models.User
import com.st_louis.ui.admin.AdminDashboardActivity
import com.st_louis.ui.bursar.BursarDashboardActivity
import com.st_louis.ui.parent.ParentDashboardActivity
import com.st_louis.ui.student.StudentDashboardActivity
import com.st_louis.ui.teacher.TeacherDashboardActivity
import com.st_louis.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val roles = arrayOf("ADMIN", "TEACHER", "BURSAR", "STUDENT", "PARENT")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            performLogin()
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Optional: Press enter on password field to login
        binding.passwordEditText.setOnEditorActionListener { _, _, _ ->
            performLogin()
            true
        }
    }

    private fun performLogin() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val role = binding.roleSpinner.selectedItem.toString()

        // Validate input
        when {
            username.isEmpty() -> {
                binding.usernameEditText.error = "Username is required"
                binding.usernameEditText.requestFocus()
                return
            }
            password.isEmpty() -> {
                binding.passwordEditText.error = "Password is required"
                binding.passwordEditText.requestFocus()
                return
            }
            role.isEmpty() -> {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Show loading state
        binding.loginButton.isEnabled = false
        binding.loginButton.text = "Logging in..."

        lifecycleScope.launch {
            val result = authRepository.login(username, password, role)

            // Reset button state
            binding.loginButton.isEnabled = true
            binding.loginButton.text = getString(R.string.login)

            when (result) {
                is Result.Success -> {
                    saveUserSession(result.data)
                    navigateToDashboard(result.data.role)
                }
                is Result.Error -> {
                    showError(result.exception.message ?: "Login failed")
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    private fun saveUserSession(user: User) {
        // Save user data and token to SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("user_id", user.id)
            putString("username", user.username)
            putString("email", user.email)
            putString("role", user.role)
            putString("token", user.token)
            putString("first_name", user.firstName)
            putString("last_name", user.lastName)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role.uppercase()) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "TEACHER" -> Intent(this, TeacherDashboardActivity::class.java)
            "BURSAR" -> Intent(this, BursarDashboardActivity::class.java)
            "STUDENT" -> Intent(this, StudentDashboardActivity::class.java)
            "PARENT" -> Intent(this, ParentDashboardActivity::class.java)
            else -> {
                Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_SHORT).show()
                Intent(this, AdminDashboardActivity::class.java)
            }
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, "Login failed: $message", Toast.LENGTH_LONG).show()
    }
}