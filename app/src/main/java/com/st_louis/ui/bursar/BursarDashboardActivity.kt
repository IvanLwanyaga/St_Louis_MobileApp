package com.st_louis.ui.bursar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import com.st_louis.R
import com.st_louis.databinding.ActivityBursarDashboardBinding
import com.st_louis.databinding.ItemRecentPaymentBinding
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BursarDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBursarDashboardBinding
    private val viewModel: BursarDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBursarDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupObservers()
        setupChart()
        setupQuickActions()
        loadRecentPayments()
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
                R.id.nav_payments -> { NavigationRoutes.navigateToPayments(this); true }
                R.id.nav_expenses -> {  NavigationRoutes.navigateToExpenses(this); true }
                R.id.nav_reports -> { NavigationRoutes.navigateToReports(this); true }
                else -> false
            }
        }
    }

    private fun setupObservers() {
        binding.tvGreeting.text = "Good morning,"
        binding.tvAvatar.text = "SW"
        
        viewModel.bursarName.observe(this) { binding.tvBursarName.text = it }
        viewModel.todayCollection.observe(this) { binding.tvTodayCollection.text = it.replace("KES", "UGX") }
        viewModel.pendingFees.observe(this) { binding.tvPendingFees.text = it.replace("KES", "UGX") }


    }

    private fun setupQuickActions() {
        binding.btnIssueReceipt.setOnClickListener { Toast.makeText(this, "Opening Receipt System...", Toast.LENGTH_SHORT).show() }
        binding.btnRecordPayment.setOnClickListener { Toast.makeText(this, "Opening Payment Recording...", Toast.LENGTH_SHORT).show() }
        binding.btnFeeStatement.setOnClickListener { Toast.makeText(this, "Generating Fee Statement...", Toast.LENGTH_SHORT).show() }
        binding.btnSmsDefaulters.setOnClickListener { Toast.makeText(this, "Sending SMS to Defaulters...", Toast.LENGTH_SHORT).show() }
        binding.btnExpenses.setOnClickListener { Toast.makeText(this, "Opening Expenses Manager...", Toast.LENGTH_SHORT).show() }
        binding.btnExportReport.setOnClickListener { NavigationRoutes.navigateToReports(this) }
    }

    private fun loadRecentPayments() {
        val payments = listOf(
            PaymentData("Peter Kamagala", "Grade 4A", "M-Pesa", "13 Jun 2026", "UGX 15,000"),
            PaymentData("Mary Namubi", "Grade 2B", "Bank Transfer", "12 Jun 2026", "UGX42,500"),
            PaymentData("Deo kato", "Pre-Unit", "Cash", "12 Jun 2026", "UGX 5,000"),
            PaymentData("Faith Kirabo", "Grade 6", "M-Pesa", "11 Jun 2026", "UGX 20,000")
        )

        binding.recentPaymentsList.removeAllViews()
        payments.forEach { payment ->
            val itemBinding = ItemRecentPaymentBinding.inflate(LayoutInflater.from(this), binding.recentPaymentsList, false)
            itemBinding.tvStudentName.text = payment.name
            itemBinding.tvPaymentDate.text = "${payment.date} · ${payment.method}"
            itemBinding.tvAmount.text = payment.amount
            
            itemBinding.root.setOnClickListener {
                showPaymentDetails(payment)
            }
            binding.recentPaymentsList.addView(itemBinding.root)
        }
    }

    private fun showPaymentDetails(payment: PaymentData) {
        AlertDialog.Builder(this)
            .setTitle("Payment Details")
            .setMessage("""
                Student: ${payment.name}
                Class: ${payment.className}
                Date: ${payment.date}
                Method: ${payment.method}
                Amount: ${payment.amount}
                Status: Verified
            """.trimIndent())
            .setPositiveButton("Close", null)
            .setNeutralButton("Print Receipt") { _, _ ->
                Toast.makeText(this, "Sending to Printer...", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun setupChart() {
        val chart = binding.feeCollectionChart
        val entries = listOf(
            BarEntry(0f, 150000f),
            BarEntry(1f, 220000f),
            BarEntry(2f, 180000f),
            BarEntry(3f, 300000f),
            BarEntry(4f, 250000f)
        )

        val dataSet = BarDataSet(entries, "Weekly Collection").apply {
            color = Color.parseColor("#E64A19")
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        chart.data = BarData(dataSet)
        chart.description.isEnabled = false
        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri"))
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }
        chart.animateY(1000)
        chart.invalidate()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Already here */ }
            R.id.nav_payments -> {  NavigationRoutes.navigateToPayments(this) ;true }
            R.id.nav_expenses -> { NavigationRoutes.navigateToExpenses(this) ; true}
            R.id.nav_reports -> { NavigationRoutes.navigateToReports(this) }
            R.id.nav_messages -> { NavigationRoutes.navigateToMessages(this) ; true }
            R.id.nav_profile -> { NavigationRoutes.navigateToProfile(this) ; true }
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

    data class PaymentData(
        val name: String,
        val className: String,
        val method: String,
        val date: String,
        val amount: String
    )
}
