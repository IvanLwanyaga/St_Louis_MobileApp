package com.st_louis.ui.teacher

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.adapters.StudentResultAdapter
import com.st_louis.models.Student
import com.st_louis.viewmodels.ResultsViewModel

class EnterResultsActivity : AppCompatActivity() {

    private lateinit var viewModel: ResultsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentResultAdapter
    private lateinit var btnSubmitResults: Button
    private lateinit var btnCancel: Button
    private lateinit var btnAddMore: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvExamTitle: TextView
    private lateinit var spinnerSection: Spinner
    private lateinit var spinnerExam: Spinner
    private lateinit var spinnerClass: Spinner
    private lateinit var editTextSubject: EditText
    private lateinit var tvTotalStudents: TextView
    private lateinit var tvEnteredCount: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var tvGradingInfo: TextView

    private var teacherId: String = ""
    private var selectedSection: String = ""
    private var selectedExam: String = ""
    private var selectedClass: String = ""
    private var currentSubject: String = ""

    // Track all entered results for this session
    private val allResults = mutableMapOf<String, MutableList<ResultEntry>>()

    data class ResultEntry(
        val studentId: String,
        val studentName: String,
        val score: Int,
        val grade: String,
        val subject: String,
        val examType: String,
        val section: String,
        val className: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_results)

        teacherId = intent.getStringExtra("teacher_id") ?: ""

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupSpinners()
        setupObservers()
        setupClickListeners()

        // Load initial data
        viewModel.loadSections()
        viewModel.loadExams(teacherId)
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvStudents)
        btnSubmitResults = findViewById(R.id.btnSubmitResults)
        btnCancel = findViewById(R.id.btnCancel)
        btnAddMore = findViewById(R.id.btnAddMore)
        progressBar = findViewById(R.id.progressBar)
        tvExamTitle = findViewById(R.id.tvExamTitle)
        spinnerSection = findViewById(R.id.spinnerSection)
        spinnerExam = findViewById(R.id.spinnerExam)
        spinnerClass = findViewById(R.id.spinnerClass)
        editTextSubject = findViewById(R.id.editTextSubject)
        tvTotalStudents = findViewById(R.id.tvTotalStudents)
        tvEnteredCount = findViewById(R.id.tvEnteredCount)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvGradingInfo = findViewById(R.id.tvGradingInfo)

        supportActionBar?.title = "Enter Results"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ResultsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = StudentResultAdapter(
            isPrePrimary = { selectedSection == "Pre-Primary" },
            onResultChanged = { student, score, grade ->
                // Store result temporarily
                viewModel.updateStudentResult(student.id, score, grade)
                updateEnteredCount()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSpinners() {
        // Section Spinner (Pre-Primary / Primary)
        val sections = listOf("Select Section", "Pre-Primary", "Primary")
        val sectionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sections)
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSection.adapter = sectionAdapter

        spinnerSection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSection = parent.getItemAtPosition(position).toString()
                if (selectedSection != "Select Section") {
                    updateGradingInfo()
                    loadClasses()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Exam Spinner
        val examAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf("Select Exam")
        )
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExam.adapter = examAdapter

        spinnerExam.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedExam = parent.getItemAtPosition(position).toString()
                if (selectedExam != "Select Exam" && selectedClass.isNotEmpty()) {
                    loadStudents()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Class Spinner
        val classAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf("Select Class")
        )
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass.adapter = classAdapter

        spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedClass = parent.getItemAtPosition(position).toString()
                if (selectedClass != "Select Class" && selectedExam != "Select Exam") {
                    loadStudents()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateGradingInfo() {
        tvGradingInfo.visibility = View.VISIBLE
        val info = when (selectedSection) {
            "Pre-Primary" -> "Grading: Exceeding (80-100) | Meeting (60-79) | Approaching (40-59) | Beginning (0-39)"
            "Primary" -> "Grading: A(80-100) B(70-79) C(60-69) D(50-59) E(0-49)"
            else -> ""
        }
        tvGradingInfo.text = info
    }

    private fun loadClasses() {
        val adapter = spinnerClass.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.add("Select Class")

        // Load classes based on section
        viewModel.loadClasses(teacherId, selectedSection)
    }

    private fun loadStudents() {
        val subject = editTextSubject.text.toString()
        if (subject.isEmpty()) {
            Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show()
            return
        }
        currentSubject = subject
        tvExamTitle.text = "$selectedExam - $subject"

        viewModel.loadStudents(teacherId, selectedClass, selectedExam, subject)
    }

    private fun setupObservers() {
        // Observe sections
        viewModel.sections.observe(this) { sections ->
            val adapter = spinnerSection.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.add("Select Section")
            adapter.addAll(sections)
            adapter.notifyDataSetChanged()
        }

        // Observe exams
        viewModel.exams.observe(this) { exams ->
            val adapter = spinnerExam.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.add("Select Exam")
            adapter.addAll(exams)
            adapter.notifyDataSetChanged()
        }

        // Observe classes
        viewModel.classes.observe(this) { classes ->
            val adapter = spinnerClass.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.add("Select Class")
            adapter.addAll(classes)
            adapter.notifyDataSetChanged()
        }

        // Observe students
        viewModel.students.observe(this) { students ->
            if (students.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                tvEmptyState.text = "No students found in this class"
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(students)
                updateUI(students)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
            btnSubmitResults.isEnabled = !isLoading
            btnAddMore.isEnabled = !isLoading
        }

        // Observe results submission result
        viewModel.resultsResult.observe(this) { result ->
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
        // Submit Results
        btnSubmitResults.setOnClickListener {
            val subject = editTextSubject.text.toString()
            if (subject.isEmpty()) {
                Toast.makeText(this, "Please enter subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedExam == "Select Exam" || selectedClass == "Select Class") {
                Toast.makeText(this, "Please select exam and class", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val enteredCount = viewModel.getEnteredCount()
            val totalStudents = adapter.itemCount

            if (enteredCount < totalStudents) {
                showConfirmDialog(
                    "⚠️ Incomplete Results",
                    "You have entered results for $enteredCount out of $totalStudents students.\nDo you want to submit anyway?"
                ) {
                    submitResults()
                }
            } else {
                submitResults()
            }
        }

        // Cancel Button
        btnCancel.setOnClickListener {
            showConfirmDialog(
                "Cancel Entry",
                "Are you sure you want to cancel? All entered data will be lost."
            ) {
                finish()
            }
        }

        // Add More Button - Clear current selection but keep entered data
        btnAddMore.setOnClickListener {
            showAddMoreDialog()
        }
    }

    private fun submitResults() {
        val subject = editTextSubject.text.toString()
        viewModel.submitResults(teacherId, selectedClass, selectedExam, subject, selectedSection)
    }

    private fun updateUI(students: List<Student>) {
        val total = students.size
        tvTotalStudents.text = "Total: $total students"
        updateEnteredCount()
    }

    private fun updateEnteredCount() {
        val entered = viewModel.getEnteredCount()
        val total = adapter.itemCount
        tvEnteredCount.text = "Entered: $entered/$total"

        // Change color based on progress
        if (entered == total && total > 0) {
            tvEnteredCount.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            tvEnteredCount.setTextColor(getColor(android.R.color.holo_orange_dark))
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
            .setMessage("Results saved successfully!\n\nDo you want to add more results?")
            .setPositiveButton("Add More") { _, _ ->
                resetForm()
            }
            .setNegativeButton("Done") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showAddMoreDialog() {
        val options = arrayOf(
            "Same Section, Same Class, Different Subject",
            "Same Section, Different Class",
            "Different Section",
            "Cancel"
        )

        AlertDialog.Builder(this)
            .setTitle("Add More Results")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Same section, same class, different subject
                        editTextSubject.text.clear()
                        editTextSubject.requestFocus()
                        Toast.makeText(this, "Enter new subject and click load", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        // Same section, different class
                        spinnerClass.setSelection(0)
                        Toast.makeText(this, "Select a different class", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        // Different section
                        spinnerSection.setSelection(0)
                        Toast.makeText(this, "Select a different section", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        // Cancel
                    }
                }
            }
            .show()
    }

    private fun resetForm() {
        editTextSubject.text.clear()
        spinnerSection.setSelection(0)
        spinnerClass.setSelection(0)
        spinnerExam.setSelection(0)
        adapter.clearList()
        tvTotalStudents.text = "Total: 0 students"
        tvEnteredCount.text = "Entered: 0/0"
        tvExamTitle.text = "Select Exam"
        tvEmptyState.visibility = View.VISIBLE
        tvEmptyState.text = "Select section, class and subject to load students"
        recyclerView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (viewModel.getEnteredCount() > 0) {
            showConfirmDialog(
                "Exit without saving?",
                "You have entered results that haven't been saved. Are you sure you want to exit?"
            ) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}