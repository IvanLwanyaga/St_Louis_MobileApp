package com.st_louis.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.st_louis.R
import com.st_louis.databinding.ActivityStudentDashboardBinding
import com.st_louis.databinding.ItemDueAssignmentBinding
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityStudentDashboardBinding
    private val viewModel: StudentDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupObservers()
        loadDueAssignments()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.login, R.string.login
        )
        toggle.isDrawerIndicatorEnabled = false 
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupObservers() {
        binding.tvGreeting.text = "Hi there,"
        binding.tvAvatar.text = "PK"
        
        viewModel.studentName.observe(this) { binding.tvStudentName.text = it }
        viewModel.className.observe(this) { binding.tvClass.text = it }
        viewModel.rank.observe(this) { binding.tvRank.text = it }
        viewModel.avgScore.observe(this) { binding.tvAvgScore.text = it }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_results -> { /* NavigationRoutes.navigateToResults(this); */ true }
                R.id.nav_assignments -> { /* NavigationRoutes.navigateToAssignments(this); */ true }
                R.id.nav_fees -> { /* NavigationRoutes.navigateToFees(this); */ true }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnTimetableFull.setOnClickListener {
            NavigationRoutes.navigateToTimetable(this)
        }
        binding.btnViewAllAssignments.setOnClickListener {
            // NavigationRoutes.navigateToAssignments(this)
        }
    }

    private fun loadDueAssignments() {
        val assignments = listOf(
            Assignment("Math: Algebra Quiz", "Today, 4:00 PM", "Calculate the value of X in the given equations..."),
            Assignment("English: Essay", "Tomorrow, 8:00 AM", "Write a 500-word essay on 'The Importance of Education'."),
            Assignment("Science: Lab Report", "15 Jun, 10:00 AM", "Submit the findings of the photosynthesis experiment.")
        )

        binding.assignmentList.removeAllViews()
        assignments.forEach { item ->
            val itemBinding = ItemDueAssignmentBinding.inflate(LayoutInflater.from(this), binding.assignmentList, false)
            itemBinding.tvAssignmentTitle.text = item.title
            itemBinding.tvDueDate.text = "Due: ${item.dueDate}"
            
            itemBinding.root.setOnClickListener {
                showAssignmentDialog(item)
            }
            binding.assignmentList.addView(itemBinding.root)
        }
    }

    private fun showAssignmentDialog(assignment: Assignment) {
        AlertDialog.Builder(this)
            .setTitle(assignment.title)
            .setMessage("${assignment.instructions}\n\nDue Date: ${assignment.dueDate}")
            .setPositiveButton("Submit Task") { _, _ ->
                Toast.makeText(this, "Redirecting to submission portal...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Already here */ }
            R.id.nav_results -> { /* Navigate to Results */ }
            R.id.nav_assignments -> { /* Navigate to Assignments */ }
            R.id.nav_attendance -> { /* Navigate to Attendance */ }
            R.id.nav_timetable -> { NavigationRoutes.navigateToTimetable(this) }
            R.id.nav_fees -> { /* Navigate to Fees */ }
            R.id.nav_messages -> { /* Navigate to Messages */ }
            R.id.nav_profile -> { /* Navigate to Profile */ }
            R.id.nav_logout -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        NavigationRoutes.navigateToLogin(this)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    data class Assignment(val title: String, val dueDate: String, val instructions: String)
}
