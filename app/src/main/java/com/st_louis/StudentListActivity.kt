package com.st_louis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.adapters.StudentListAdapter
import com.st_louis.models.Student
import com.st_louis.utils.PreferenceManager
import com.st_louis.ViewModels.StudentsListViewModel

class StudentListActivity : AppCompatActivity() {

    private lateinit var viewModel: StudentsListViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var spinnerClass: Spinner
    private lateinit var tvTotalStudents: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var btnFilter: Button
    private lateinit var tvTeacherName: TextView

    private var teacherId: String = ""
    private var userRole: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        val preferenceManager = PreferenceManager.getInstance(this)
        teacherId = intent.getStringExtra("teacher_id") ?: preferenceManager.getCurrentUser()?.id ?: ""
        userRole = intent.getStringExtra("user_role") ?: preferenceManager.getUserRole() ?: "TEACHER"

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupSpinner()
        setupSearchView()
        setupObservers()
        setupClickListeners()

        if (teacherId.isNotEmpty()) {
            viewModel.loadClasses(teacherId)
            viewModel.loadStudents(teacherId, null)
        } else {
            Toast.makeText(this, "Teacher ID not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.rvStudents)
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)
        spinnerClass = findViewById(R.id.spinnerClass)
        tvTotalStudents = findViewById(R.id.tvTotalStudents)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        btnFilter = findViewById(R.id.btnFilter)
        tvTeacherName = findViewById(R.id.tvTeacherName)

        supportActionBar?.title = if (userRole == "ADMIN") "All Students" else "My Students"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val user = PreferenceManager.getInstance(this).getCurrentUser()
        tvTeacherName.text = "👋 ${user?.fullName ?: "Teacher"}"
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[StudentsListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = StudentListAdapter { student ->
            // Assume StudentProfileActivity exists in com.st_louis
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("student_id", student.id)
            intent.putExtra("student_name", student.name)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSpinner() {
        val classAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf("All Classes")
        )
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass.adapter = classAdapter

        spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedClass = parent.getItemAtPosition(position).toString()
                if (selectedClass == "All Classes") {
                    viewModel.loadStudents(teacherId, null)
                } else {
                    viewModel.loadStudents(teacherId, selectedClass)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchStudents(it)
                    } else {
                        viewModel.loadStudents(teacherId, null)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.loadStudents(teacherId, null)
                } else {
                    viewModel.searchStudents(newText)
                }
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.students.observe(this) { students ->
            if (students.isNullOrEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                tvEmptyState.text = "No students found"
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(students)
                tvTotalStudents.text = "👥 Total: ${students.size} students"
            }
        }

        viewModel.classes.observe(this) { classes ->
            val adapter = spinnerClass.adapter as ArrayAdapter<String>
            adapter.clear()
            adapter.add("All Classes")
            if (!classes.isNullOrEmpty()) {
                adapter.addAll(classes.map { it.name })
            }
            adapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                tvEmptyState.visibility = View.VISIBLE
                tvEmptyState.text = "Error loading students"
            }
        }
    }

    private fun setupClickListeners() {
        btnFilter.setOnClickListener {
            Toast.makeText(this, "Filter options coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}