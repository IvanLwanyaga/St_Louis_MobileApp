package com.st_louis.ui.parent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.st_louis.R
import com.st_louis.databinding.ActivityParentDashboardBinding
import com.st_louis.navigation.NavigationRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityParentDashboardBinding
    private val viewModel: ParentDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupObservers()
        setupContactActions()
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
                R.id.nav_children -> { Toast.makeText(this, "Children List", Toast.LENGTH_SHORT).show(); true }
                R.id.nav_fees -> { /* NavigationRoutes.navigateToFees(this); */ true }
                R.id.nav_notices -> { Toast.makeText(this, "All Notices", Toast.LENGTH_SHORT).show(); true }
                else -> false
            }
        }
    }

    private fun setupObservers() {
        binding.tvGreeting.text = "Welcome back,"
        binding.tvAvatar.text = "DK"
        
        viewModel.parentName.observe(this) { binding.tvParentName.text = it }
        viewModel.childInfo.observe(this) { binding.tvChildInfo.text = it }
        viewModel.childAttendance.observe(this) { binding.tvChildAttendance.text = it }
        viewModel.feeBalance.observe(this) { binding.tvFeeBalance.text = it }
    }

    private fun setupContactActions() {
        binding.btnCallSchool.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+256700000000") // Example school number
            }
            startActivity(intent)
        }

        binding.btnEmailSchool.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:info@stlouis.edu.ug")
                putExtra(Intent.EXTRA_SUBJECT, "Parent Inquiry - ${binding.tvParentName.text}")
            }
            startActivity(intent)
        }

        binding.btnMessageSchool.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:+256700000000")
                putExtra("sms_body", "Hello St. Louis, this is ${binding.tvParentName.text}...")
            }
            startActivity(intent)
        }

        binding.btnViewAllNotices.setOnClickListener {
            Toast.makeText(this, "Opening full notice board...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Home */ }
            R.id.nav_children -> { /* Children List */ }
            R.id.nav_results -> { /* Results */ }
            R.id.nav_attendance -> { /* Attendance History */ }
            R.id.nav_fees -> { /* Fee Tracking */ }
            R.id.nav_notices -> { /* School Notices */ }
            R.id.nav_messages -> { /* Chat with School */ }
            R.id.nav_profile -> { /* Parent Profile */ }
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
