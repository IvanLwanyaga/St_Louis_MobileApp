package com.st_louis.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.st_louis.R
import com.st_louis.models.User
import com.st_louis.ui.teacher.TeacherDashboardViewModel
import com.st_louis.utils.PreferenceManager
import com.st_louis.LoginActivity
import com.st_louis.ProfileActivity
import com.st_louis.ClassesActivity
import com.st_louis.StudentListActivity
import com.st_louis.MarkAttendanceActivity
import com.st_louis.EnterResultsActivity

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: TeacherDashboardViewModel
    private lateinit var preferenceManager: PreferenceManager
    private var currentUser: User? = null

    // Views
    private lateinit var tvGreeting: TextView
    private lateinit var tvTeacherName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvAvatar: TextView
    private lateinit var tvTotalStudents: TextView
    private lateinit var tvMarked: TextView
    private lateinit var tvNotMarked: TextView
    private lateinit var tvPendingTasks: TextView

    // Quick action buttons
    private lateinit var btnMarkAttendance: View
    private lateinit var btnEnterResults: View
    private lateinit var btnViewStudents: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        initializeViews()
        setupViewModel()
        loadUserData()
        observeViewModel()
        setupClickListeners()
        setupBottomNavigation()
        setupDrawer()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)

        // Header views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvTeacherName = findViewById(R.id.tvTeacherName)
        tvRole = findViewById(R.id.tvRole)
        tvAvatar = findViewById(R.id.tvAvatar)

        // Stats cards
        tvTotalStudents = findViewById(R.id.tvTotalStudents)
        tvMarked = findViewById(R.id.tvMarked)
        tvNotMarked = findViewById(R.id.tvNotMarked)
        tvPendingTasks = findViewById(R.id.tvPendingTasks)

        // Quick action buttons
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance)
        btnEnterResults = findViewById(R.id.btnEnterResults)
        btnViewStudents = findViewById(R.id.btnViewStudents)

        preferenceManager = PreferenceManager.getInstance(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TeacherDashboardViewModel::class.java]
    }

    private fun loadUserData() {
        currentUser = preferenceManager.getCurrentUser()
        currentUser?.let { user ->
            displayUserInfo(user)
            viewModel.loadDashboardData(user)
        } ?: run {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun displayUserInfo(user: User) {
        val greeting = getTimeBasedGreeting()
        tvGreeting.text = greeting

        tvTeacherName.text = user.fullName
        tvRole.text = "Teacher"

        val initials = user.fullName.split(" ")
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
        tvAvatar.text = initials
    }

    private fun getTimeBasedGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            else -> "Good evening,"
        }
    }

    private fun observeViewModel() {
        viewModel.dashboardData.observe(this) { data ->
            data?.let {
                tvTotalStudents.text = it.totalStudents.toString()
                tvMarked.text = "Marked: ${it.markedAttendance}"
                tvNotMarked.text = "Not Marked: ${it.unmarkedAttendance}"

                if (it.pendingTasks.isNotEmpty()) {
                    tvPendingTasks.text = it.pendingTasks.joinToString("\n")
                } else {
                    tvPendingTasks.text = "No pending tasks ✅"
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // You can add a progress bar here if needed
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        // Quick Action: Mark Attendance
        btnMarkAttendance.setOnClickListener {
            navigateToMarkAttendance()
        }

        // Quick Action: Enter Results
        btnEnterResults.setOnClickListener {
            navigateToEnterResults()
        }

        // Quick Action: View Students
        btnViewStudents.setOnClickListener {
            navigateToViewStudents()
        }
    }

    // Navigation Methods
    private fun navigateToMarkAttendance() {
        val intent = Intent(this, MarkAttendanceActivity::class.java)
        currentUser?.let {
            intent.putExtra("teacher_id", it.id)
            intent.putExtra("teacher_name", it.fullName)
        }
        startActivity(intent)
    }

    private fun navigateToEnterResults() {
        val intent = Intent(this, EnterResultsActivity::class.java)
        currentUser?.let {
            intent.putExtra("teacher_id", it.id)
            intent.putExtra("teacher_name", it.fullName)
        }
        startActivity(intent)
    }

    private fun navigateToViewStudents() {
        val intent = Intent(this, StudentListActivity::class.java)
        currentUser?.let {
            intent.putExtra("teacher_id", it.id)
        }
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        currentUser?.let {
            intent.putExtra("user_id", it.id)
        }
        startActivity(intent)
    }

    private fun navigateToClasses() {
        val intent = Intent(this, ClassesActivity::class.java)
        currentUser?.let {
            intent.putExtra("teacher_id", it.id)
        }
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on dashboard
                    true
                }
                R.id.nav_classes -> {
                    navigateToClasses()
                    true
                }
                R.id.nav_students -> {
                    navigateToViewStudents()
                    true
                }
                R.id.nav_profile -> {
                    navigateToProfile()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDrawer() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home-> {
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_profile -> {
                    drawerLayout.closeDrawers()
                    navigateToProfile()
                    true
                }
                R.id.nav_classes -> {
                    drawerLayout.closeDrawers()
                    navigateToClasses()
                    true
                }
                R.id.nav_students -> {
                    drawerLayout.closeDrawers()
                    navigateToViewStudents()
                    true
                }
                R.id.nav_attendance -> {
                    drawerLayout.closeDrawers()
                    navigateToMarkAttendance()
                    true
                }
                R.id.nav_results -> {
                    drawerLayout.closeDrawers()
                    navigateToEnterResults()
                    true
                }
                R.id.nav_logout -> {
                    drawerLayout.closeDrawers()
                    handleLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleLogout() {
        viewModel.logout()
        preferenceManager.clearSession()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }
}