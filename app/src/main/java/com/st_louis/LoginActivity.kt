package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.st_louis.ViewModels.AuthViewModel
import com.st_louis.ViewModels.AuthViewModelFactory
import com.st_louis.data.ApiClient
import com.st_louis.data.repository.AuthRepository
import com.st_louis.models.LoginRequest
import com.st_louis.ui.admin.AdminDashboardActivity
import com.st_louis.ui.teacher.TeacherDashboardActivity
import com.st_louis.ui.student.StudentDashboardActivity
import com.st_louis.ui.parent.ParentDashboardActivity
import com.st_louis.ui.bursar.BursarDashboardActivity
import com.st_louis.utils.PreferenceManager

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceManager = PreferenceManager.getInstance(this)
        
        if (preferenceManager.isLoggedIn()) {
            navigateToDashboard(preferenceManager.getUserRole() ?: "")
            return
        }

        initViews()
        setupViewModel()
        observeViewModel()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(LoginRequest(email, password))
            }
        }
    }

    private fun setupViewModel() {
        val repository = AuthRepository(ApiClient.apiService)
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(this) { response ->
            if (response.isSuccessful && response.body()?.data != null) {
                val loginData = response.body()!!.data!!
                preferenceManager.saveUser(loginData.user)
                preferenceManager.saveToken(loginData.token)
                navigateToDashboard(loginData.user.role.name.lowercase())
            } else {
                Toast.makeText(this, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role.lowercase()) {
            "admin" -> Intent(this, AdminDashboardActivity::class.java)
            "teacher" -> Intent(this, TeacherDashboardActivity::class.java)
            "student" -> Intent(this, StudentDashboardActivity::class.java)
            "parent" -> Intent(this, ParentDashboardActivity::class.java)
            "bursar" -> Intent(this, BursarDashboardActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}