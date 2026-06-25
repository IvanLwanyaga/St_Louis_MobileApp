package com.st_louis.ui.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.navigation.NavigationView
import com.st_louis.LoginActivity
import com.st_louis.R
import com.st_louis.databinding.ActivityAdminDashboardBinding
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val viewModel: AdminDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupObservers()
        setupCharts()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Open drawer when clicking the custom menu button
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.login, R.string.login
        )
        // Disable default drawer icon because we have a custom ImageView (btnMenu)
        toggle.isDrawerIndicatorEnabled = false 
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_students -> { NavigationRoutes.navigateToStudentManagement(this); true }
                R.id.nav_fees -> { NavigationRoutes.navigateToFinance(this); true }
                R.id.nav_reports -> { NavigationRoutes.navigateToReports(this); true }
                R.id.nav_schedule ->{NavigationRoutes.navigateToTimetableManagement(this);true}
                R.id.nav_settings -> {NavigationRoutes.navigateToSettings(this);true}
                else -> false
            }
        }
    }

    private fun setupObservers() {
        binding.tvGreeting.text = "Good morning,"
        binding.tvAdminName.text = "Dr. Jordan Ivan"
        binding.tvAdminRole.text = "School Principal"
        binding.tvAvatar.text = "JI"
        binding.tvCurrentDate.text = "Mon, 13 Jun 2026"
        binding.tvTermInfo.text = "Term 2 — Week 8"

        viewModel.studentCount.observe(this) { binding.tvStudentCount.text = it }
        viewModel.staffCount.observe(this) { binding.tvStaffCount.text = it }
        viewModel.attendanceRate.observe(this) { binding.tvAttendanceRate.text = it }
        viewModel.feesCollected.observe(this) { binding.tvFeesCollected.text = it }
    }

    private fun setupCharts() {
        setupEnrollmentLineChart(binding.enrollmentChart)
        setupFeeDonutChart(binding.feeDonutChart)
    }

    private fun setupEnrollmentLineChart(chart: LineChart) {
        val entries = listOf(
            Entry(0f, 820f), Entry(1f, 850f), Entry(2f, 870f),
            Entry(3f, 900f), Entry(4f, 920f), Entry(5f, 950f)
        )

        val dataSet = LineDataSet(entries, "Students").apply {
            color = Color.parseColor("#1E88E5")
            valueTextColor = Color.BLACK
            lineWidth = 2.5f
            setCircleColor(Color.parseColor("#1E88E5"))
            circleRadius = 5f
            setDrawCircleHole(true)
        }

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.xAxis.apply { 
            valueFormatter = IndexAxisValueFormatter(listOf("Jan","Feb","Mar","Apr","May","Jun"))
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
        }
        chart.animateX(1000)
        chart.invalidate()
    }

    private fun setupFeeDonutChart(chart: PieChart) {
        val entries = listOf(
            PieEntry(68f, "Paid"),
            PieEntry(18f, "Partial"),
            PieEntry(14f, "Pending")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#F44336")
            )
            sliceSpace = 3f
        }

        chart.data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(chart)) }
        chart.apply {
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 55f
            transparentCircleRadius = 60f
            description.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
        }
        chart.invalidate()
    }

    private fun setupListeners() {
        binding.btnAddStudent.setOnClickListener { NavigationRoutes.navigateToStudentManagement(this) }
        binding.btnTimetable.setOnClickListener { NavigationRoutes.navigateToTimetable(this) }
        binding.btnSendNotice.setOnClickListener { /* Navigate to Notice */ }
        binding.btnReportsQuick.setOnClickListener { NavigationRoutes.navigateToReports(this) }
        binding.btnFeeSetup.setOnClickListener { NavigationRoutes.navigateToFinance(this) }
        binding.btnAttendance.setOnClickListener { /* Navigate to Attendance */ }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Already here */ }
            R.id.nav_students -> NavigationRoutes.navigateToStudentManagement(this)
            R.id.nav_teachers -> NavigationRoutes.navigateToTeacherManagement(this)
            R.id.nav_bursars -> NavigationRoutes.navigateToBursarManagement(this)
            R.id.nav_timetable -> NavigationRoutes.navigateToTimetable(this)
            R.id.nav_fees -> NavigationRoutes.navigateToFinance(this)
            R.id.nav_reports -> NavigationRoutes.navigateToReports(this)
            R.id.nav_settings -> NavigationRoutes.navigateToSettings(this)
            R.id.nav_logout -> logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
