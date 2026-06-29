package com.st_louis.ui.attendance

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.st_louis.R
import com.st_louis.data.ApiClient
import com.st_louis.databinding.ActivityAttendanceBinding

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var viewModel: AttendanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupObservers()
        setupListeners()
        
        viewModel.loadAttendance("S001")
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { 
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewModel() {
        // Manual instantiation since Hilt is not yet configured on this branch
        val apiService = ApiClient.getApiService()
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AttendanceViewModel(apiService) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AttendanceViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.attendancePercentage.observe(this) { percentage ->
            binding.tvAttendancePercent.text = "$percentage%"
            try {
                binding.progressBar.progress = percentage.toInt()
            } catch (e: Exception) {
                binding.progressBar.progress = 0
            }
            
            val statusMessage = try {
                val percent = percentage.toDouble()
                when {
                    percent >= 90 -> "Excellent attendance! Keep it up."
                    percent >= 75 -> "Good attendance."
                    else -> "Attendance is below target. Please improve."
                }
            } catch (e: Exception) {
                "Loading history..."
            }
            binding.tvStatusMessage.text = statusMessage
        }

        viewModel.attendanceRecords.observe(this) { records ->
            // View records in calendar
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnMarkPresent.setOnClickListener {
            viewModel.markAttendance("present")
        }
        
        binding.btnMarkAbsent.setOnClickListener {
            viewModel.markAttendance("absent")
        }
    }
}
