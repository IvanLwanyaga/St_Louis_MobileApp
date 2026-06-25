package com.st_louis

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.st_louis.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val role = intent.getStringExtra("USER_ROLE") ?: "ADMIN"
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set start destination based on role
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        when (role) {
            "ADMIN" -> navGraph.setStartDestination(R.id.nav_home)
            "TEACHER" -> navGraph.setStartDestination(R.id.nav_teacher_home)
            "STUDENT" -> navGraph.setStartDestination(R.id.nav_student_home)
            "BURSAR" -> navGraph.setStartDestination(R.id.nav_bursar_home)
            "PARENT" -> navGraph.setStartDestination(R.id.nav_parent_home)
        }
        navController.graph = navGraph

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_teacher_home, R.id.nav_student_home, 
                  R.id.nav_bursar_home, R.id.nav_parent_home, R.id.nav_stats, R.id.nav_notifications),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)
        
        binding.navView.setNavigationItemSelectedListener(this)
        
        // Update header with role
        val headerView = binding.navView.getHeaderView(0)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val userRole = headerView.findViewById<TextView>(R.id.userRole)
        userRole.text = "$role@stlouis.com"
        userName.text = "St Louis $role"
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}