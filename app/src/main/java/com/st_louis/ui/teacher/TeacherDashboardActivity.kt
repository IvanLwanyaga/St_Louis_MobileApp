package com.st_louis.ui.teacher

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.st_louis.LoginActivity
import com.st_louis.R
import com.st_louis.databinding.ActivityTeacherDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityTeacherDashboardBinding
    private val viewModel: TeacherDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupObservers()
        setupListeners()
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
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_attendance -> { /* NavigationRoutes.navigateToAttendance(this); */ true }
                R.id.nav_marks -> { /* NavigationRoutes.navigateToMarks(this); */ true }
                R.id.nav_assignments -> { /* NavigationRoutes.navigateToAssignments(this); */ true }
                else -> false
            }
        }
    }

    private fun setupObservers() {
        binding.tvGreeting.text = "Good morning,"
        binding.tvAvatar.text = "JO"
        
        viewModel.teacherName.observe(this) { binding.tvTeacherName.text = it }
        viewModel.subject.observe(this) { binding.tvSubject.text = it }
        viewModel.classesToday.observe(this) { binding.tvClassesCount.text = it }
        viewModel.totalStudents.observe(this) { binding.tvTotalStudents.text = it }
        viewModel.avgScore.observe(this) { binding.tvAvgScore.text = it }
    }

    private fun setupListeners() {
        binding.btnTakeAttendance.setOnClickListener {
            // Handle attendance
        }
        binding.btnEnterGrades.setOnClickListener {
            // Handle grades
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Already here */ }
            R.id.nav_classes -> { /* Navigate to Classes */ }
            R.id.nav_attendance -> { /* Navigate to Attendance */ }
            R.id.nav_marks -> { /* Navigate to Marks Entry */ }
            R.id.nav_assignments -> { /* Navigate to Assignments */ }
            R.id.nav_timetable -> { NavigationRoutes.navigateToTimetable(this) }
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
}
