package com.st_louis.ui.attendance

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.st_louis.R
import com.st_louis.data.ApiClient
import com.st_louis.databinding.ActivityAttendanceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private val viewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
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
