package com.st_louis.ui.student

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.st_louis.data.ApiClient
import com.st_louis.databinding.ActivityAcademicResultsBinding
import com.st_louis.models.AcademicResultsResponse
import com.st_louis.models.ExamPeriod
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AcademicResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcademicResultsBinding
    private val viewModel: AcademicResultsViewModel by viewModels()
    private lateinit var adapter: SubjectResultsAdapter
    private var examPeriods: List<ExamPeriod> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcademicResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Load initial data
        viewModel.loadExamPeriods("S001") // Should come from session
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = SubjectResultsAdapter()
        binding.rvAcademicResults.layoutManager = LinearLayoutManager(this)
        binding.rvAcademicResults.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.examPeriods.observe(this) { periods ->
            this.examPeriods = periods
            val periodNames = periods.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerExamPeriod.adapter = adapter
        }

        viewModel.results.observe(this) { response ->
            if (response != null) {
                updateUI(response)
                binding.cvResultsSummary.visibility = View.VISIBLE
                binding.rvAcademicResults.visibility = View.VISIBLE
                binding.emptyStateView.visibility = View.GONE
            } else {
                binding.cvResultsSummary.visibility = View.GONE
                binding.rvAcademicResults.visibility = View.GONE
                binding.emptyStateView.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.spinnerExamPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPeriod = examPeriods[position]
                viewModel.loadResults("S001", selectedPeriod.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateUI(data: AcademicResultsResponse) {
        binding.tvStudentName.text = data.studentName
        binding.tvStudentDetails.text = "${data.studentClass} · ${data.academicYear}"
        binding.tvOverallAverage.text = "${data.overallAverage.toInt()}%"
        binding.tvOverallGrade.text = "Grade ${data.overallGrade} · ${data.performanceComment}"
        binding.tvClassPosition.text = formatPosition(data.classPosition)
        binding.tvTotalInClass.text = "out of ${data.totalStudents} students"

        val initials = data.studentName.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it[0].uppercase() }
            .joinToString("")
        binding.tvStudentInitials.text = initials

        adapter.submitList(data.results)
    }

    private fun formatPosition(pos: Int): String {
        val suffix = when {
            pos % 100 in 11..13 -> "th"
            pos % 10 == 1 -> "st"
            pos % 10 == 2 -> "nd"
            pos % 10 == 3 -> "rd"
            else -> "th"
        }
        return "$pos$suffix"
    }
}
