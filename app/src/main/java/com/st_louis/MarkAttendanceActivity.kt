package com.st_louis

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.adapters.StudentAttendanceAdapter
import com.st_louis.data.ApiClient
import com.st_louis.models.Student
import com.st_louis.utils.PreferenceManager
import com.st_louis.viewmodels.AttendanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MarkAttendanceActivity : AppCompatActivity() {

    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAttendanceAdapter
    private lateinit var btnSubmitAttendance: Button
    private lateinit var btnCancel: Button
    private lateinit var btnMarkAllPresent: Button
    private lateinit var btnMarkAllAbsent: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvClassName: TextView
    private lateinit var tvDate: TextView
    private lateinit var spinnerSection: Spinner
    private lateinit var spinnerClass: Spinner
    private lateinit var tvTotalStudents: TextView
    private lateinit var tvMarkedCount: TextView
    private lateinit var tvEmptyState: TextView

    private var teacherId: String = ""
    private var selectedSection: String = ""
    private var selectedClass: String = ""
    private var selectedClassId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)

        teacherId = intent.getStringExtra("teacher_id") ?: PreferenceManager.getInstance(this).getCurrentUser()?.id ?: ""

        initializeViews()
        setupRecyclerView()
        setupSpinners()
        setupObservers()
        setupClickListeners()

        // Load sections
        viewModel.loadSections()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvStudents)
        btnSubmitAttendance = findViewById(R.id.btnSubmitAttendance)
        btnCancel = findViewById(R.id.btnCancel)
        btnMarkAllPresent = findViewById(R.id.btnMarkAllPresent)
        btnMarkAllAbsent = findViewById(R.id.btnMarkAllAbsent)
        progressBar = findViewById(R.id.progressBar)
        tvClassName = findViewById(R.id.tvClassName)
        tvDate = findViewById(R.id.tvDate)
        spinnerSection = findViewById(R.id.spinnerSection)
        spinnerClass = findViewById(R.id.spinnerClass)
        tvTotalStudents = findViewById(R.id.tvTotalStudents)
        tvMarkedCount = findViewById(R.id.tvMarkedCount)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        // Set current date
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(Date())

        supportActionBar?.title = "Mark Attendance"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = StudentAttendanceAdapter(
            onAttendanceChanged = { student, isPresent ->
                viewModel.updateAttendance(student.id, isPresent)
                updateMarkedCount()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSpinners() {
        // Section Spinner
        val sections = listOf("Select Section", "Pre-Primary", "Primary")
        val sectionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sections)
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSection.adapter = sectionAdapter

        spinnerSection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSection = parent.getItemAtPosition(position).toString()
                if (selectedSection != "Select Section") {
                    viewModel.loadClasses(teacherId, selectedSection)
                } else {
                    spinnerClass.adapter = ArrayAdapter<String>(
                        this@MarkAttendanceActivity,
                        android.R.layout.simple_spinner_item,
                        listOf("Select Class")
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Class Spinner
        val classAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Select Class")
        )
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass.adapter = classAdapter

        spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parent.getItemAtPosition(position).toString()
                if (selectedClass != "Select Class") {
                    // Get class ID from the selected class name
                    val classList = viewModel.classes.value
                    classList?.find { it.name == selectedClass }?.let {
                        selectedClassId = it.id
                        viewModel.loadStudents(teacherId, selectedClassId)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupObservers() {
        // Observe classes
        viewModel.classes.observe(this) { classes ->
            val adapter = spinnerClass.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.add("Select Class")
            if (!classes.isNullOrEmpty()) {
                classes.forEach { classItem ->
                    adapter.add(classItem.name)
                }
            }
            adapter.notifyDataSetChanged()
        }

        // Observe students
        viewModel.students.observe(this) { students ->
            if (students.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                tvEmptyState.text = "No students found in this class"
                tvTotalStudents.text = "👥 Total: 0 students"
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(students)
                tvTotalStudents.text = "👥 Total: ${students.size} students"
                tvClassName.text = "Class: $selectedClass"
                updateMarkedCount()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            btnSubmitAttendance.isEnabled = !isLoading
            btnMarkAllPresent.isEnabled = !isLoading
            btnMarkAllAbsent.isEnabled = !isLoading
        }

        // Observe attendance result
        viewModel.attendanceResult.observe(this) { result ->
            result?.let {
                if (it.success) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(this, "❌ Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = "Error: $it"
            }
        }
    }

    private fun setupClickListeners() {
        // Submit Attendance
        btnSubmitAttendance.setOnClickListener {
            if (selectedClass == "Select Class" || selectedSection == "Select Section") {
                Toast.makeText(this, "Please select section and class", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val markedCount = viewModel.getMarkedCount()
            val totalStudents = adapter.itemCount

            if (markedCount == 0) {
                Toast.makeText(this, "Please mark attendance for at least one student", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (markedCount < totalStudents) {
                showConfirmDialog(
                    "⚠️ Incomplete Attendance",
                    "You have marked $markedCount out of $totalStudents students.\nDo you want to submit anyway?"
                ) {
                    submitAttendance()
                }
            } else {
                submitAttendance()
            }
        }

        // Cancel Button
        btnCancel.setOnClickListener {
            if (viewModel.getMarkedCount() > 0) {
                showConfirmDialog(
                    "Cancel Attendance",
                    "Are you sure you want to cancel? All entered data will be lost."
                ) {
                    finish()
                }
            } else {
                finish()
            }
        }

        // Mark All Present
        btnMarkAllPresent.setOnClickListener {
            val students = viewModel.students.value
            students?.let {
                adapter.markAllPresent()
                updateMarkedCount()
                Toast.makeText(this, "All students marked present", Toast.LENGTH_SHORT).show()
            }
        }

        // Mark All Absent
        btnMarkAllAbsent.setOnClickListener {
            val students = viewModel.students.value
            students?.let {
                adapter.markAllAbsent()
                updateMarkedCount()
                Toast.makeText(this, "All students marked absent", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitAttendance() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        viewModel.submitAttendance(
            teacherId = teacherId,
            classId = selectedClassId,
            className = selectedClass,
            section = selectedSection,
            date = currentDate
        )
    }

    private fun updateMarkedCount() {
        val marked = viewModel.getMarkedCount()
        val total = adapter.itemCount
        tvMarkedCount.text = "✅ Marked: $marked/$total"

        // Change color based on progress
        when {
            marked == total && total > 0 -> {
                tvMarkedCount.setTextColor(getColor(android.R.color.holo_green_dark))
            }
            marked > 0 -> {
                tvMarkedCount.setTextColor(getColor(android.R.color.holo_orange_dark))
            }
            else -> {
                tvMarkedCount.setTextColor(getColor(android.R.color.holo_red_dark))
            }
        }
    }

    private fun showConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("✅ Success!")
            .setMessage("Attendance saved successfully!")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (viewModel.getMarkedCount() > 0) {
            showConfirmDialog(
                "Exit without saving?",
                "You have marked attendance that hasn't been saved. Are you sure you want to exit?"
            ) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}