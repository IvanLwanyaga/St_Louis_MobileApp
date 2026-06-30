package com.st_louis.ui.admin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.st_louis.R
import com.st_louis.adapters.TimetablePagerAdapter
import com.st_louis.data.ApiClient
import com.st_louis.ui.admin.TimetableViewModelFactory
import com.st_louis.ui.admin.timetable.TimetableViewModel

class TimetableManagementActivity : AppCompatActivity() {

    private lateinit var viewModel: TimetableViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable_management)

        setupViews()
        setupToolbar()
        setupViewModel()
        setupViewPager()
        loadData()
    }

    private fun setupViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        viewModel = ViewModelProvider(
            this,
            TimetableViewModelFactory(apiService)
        )[TimetableViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            findViewById<ProgressBar>(R.id.progressBar).visibility =
                if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupViewPager() {
        val adapter = TimetablePagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Primary"
                else -> "Pre-Primary"
            }
        }.attach()
    }

    private fun loadData() {
        viewModel.loadClasses()
        viewModel.loadTimetableSummary()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.timetable_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_print -> {
                Toast.makeText(this, "Printing timetable...", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_edit -> {
                Toast.makeText(this, "Edit mode enabled", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_export -> {
                Toast.makeText(this, "Exporting timetable...", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}