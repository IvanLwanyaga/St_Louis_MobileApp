package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.st_louis.R
import com.st_louis.models.User
import com.st_louis.models.UserRole
import com.st_louis.utils.PreferenceManager
import com.st_louis.ViewModels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var tvAvatar: TextView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvStaffId: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvQualification: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tvSubjects: TextView
    private lateinit var tvClassesAssigned: TextView
    private lateinit var tvJoinedDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        preferenceManager = PreferenceManager.getInstance(this)
        initializeViews()
        setupViewModel()
        loadUserData()
        setupObservers()

        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeViews() {
        tvAvatar = findViewById(R.id.tvAvatar)
        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvRole = findViewById(R.id.tvRole)
        tvStaffId = findViewById(R.id.tvStaffId)
        tvDepartment = findViewById(R.id.tvDepartment)
        tvPhone = findViewById(R.id.tvPhone)
        tvGender = findViewById(R.id.tvGender)
        tvQualification = findViewById(R.id.tvQualification)
        tvExperience = findViewById(R.id.tvExperience)
        tvSubjects = findViewById(R.id.tvSubjects)
        tvClassesAssigned = findViewById(R.id.tvClassesAssigned)
        tvJoinedDate = findViewById(R.id.tvJoinedDate)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private fun loadUserData() {
        val user = preferenceManager.getCurrentUser()
        user?.let {
            displayUserInfo(it)
            viewModel.loadUserProfile(it.id)
        } ?: run {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_LONG).show()
            navigateToLogin()
        }
    }

    private fun displayUserInfo(user: User) {
        val initials = user.fullName.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
        tvAvatar.text = initials

        tvName.text = user.fullName
        tvEmail.text = user.email

        tvRole.text = when (user.role) {
            UserRole.ADMIN -> "School Administrator"
            UserRole.TEACHER -> "Teacher"
            UserRole.STUDENT -> "Student"
            UserRole.PARENT -> "Parent"
            UserRole.BURSAR -> "Bursar"
        }

        tvStaffId.text = user.staffId ?: "Not assigned"
        tvDepartment.text = user.department ?: "Not assigned"
        tvPhone.text = user.phone ?: "Not provided"
        tvGender.text = user.gender ?: "Not provided"
        tvQualification.text = user.qualification ?: "Not provided"
        tvExperience.text = if (user.yearsOfExperience != null) "${user.yearsOfExperience} years" else "Not provided"

        tvSubjects.text = if (!user.subjects.isNullOrEmpty()) "📚 ${user.subjects.joinToString(", ")}" else "No subjects assigned"
        tvClassesAssigned.text = if (!user.classesAssigned.isNullOrEmpty()) "🏫 ${user.classesAssigned.joinToString(", ")}" else "No classes assigned"

        tvJoinedDate.text = user.createdAt?.let { "Joined: ${formatDate(it)}" } ?: "Not available"
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: java.util.Date())
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(this) { user ->
            user?.let {
                displayUserInfo(it)
                preferenceManager.saveUser(it)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show() }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}