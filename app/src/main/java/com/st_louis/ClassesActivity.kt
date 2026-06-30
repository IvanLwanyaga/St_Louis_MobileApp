package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.ViewModels.ClassesViewModel
import com.st_louis.adapters.ClassScheduleAdapter
import com.st_louis.models.TeacherClass
import com.st_louis.utils.PreferenceManager

class ClassesActivity : AppCompatActivity() {

    private lateinit var viewModel: ClassesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassScheduleAdapter
    private lateinit var progressBar: View
    private lateinit var tvEmptyState: TextView
    private lateinit var tvTodayDate: TextView
    private lateinit var tvTeacherName: TextView

    private lateinit var preferenceManager: PreferenceManager
    private var teacherId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classes)

        preferenceManager = PreferenceManager.getInstance(this)
        teacherId = intent.getStringExtra("teacher_id") ?: preferenceManager.getCurrentUser()?.id ?: ""

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Load classes
        if (teacherId.isNotEmpty()) {
            viewModel.loadClasses(teacherId)
        } else {
            Toast.makeText(this, "Teacher ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvClasses)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvTodayDate = findViewById(R.id.tvTodayDate)
        tvTeacherName = findViewById(R.id.tvTeacherName)

        // Set today's date
        val dateFormat = java.text.SimpleDateFormat("EEEE, MMMM d, yyyy", java.util.Locale.getDefault())
        tvTodayDate.text = dateFormat.format(java.util.Date())

        // Set teacher name
        val user = preferenceManager.getCurrentUser()
        tvTeacherName.text = "Welcome back, ${user?.fullName ?: "Teacher"} 👋"

        supportActionBar?.title = "My Classes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ClassesViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = ClassScheduleAdapter { classItem ->
            // Navigate to class detail or mark attendance
            val intent = Intent(this, MarkAttendanceActivity::class.java)
            intent.putExtra("teacher_id", teacherId)
            intent.putExtra("class_id", classItem.id)
            intent.putExtra("class_name", classItem.name)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.classes.observe(this) { classes ->
            if (classes.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                tvEmptyState.text = "No classes scheduled for today"
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(classes)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = "Error loading classes"
            }
        }
    }

    private fun setupClickListeners() {
        // Any additional click listeners
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}