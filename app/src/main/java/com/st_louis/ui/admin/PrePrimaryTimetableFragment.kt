package com.st_louis.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.st_louis.R
import com.st_louis.adapters.PrePrimaryRoutineAdapter
import com.st_louis.adapters.ClassSelectorAdapter
import com.st_louis.models.ClassInfo
import com.st_louis.models.PrePrimaryActivity
import com.st_louis.ui.admin.TimetableViewModel
import java.text.SimpleDateFormat
import java.util.*

class PrePrimaryTimetableFragment : Fragment() {

    private lateinit var viewModel: TimetableViewModel
    private lateinit var routineAdapter: PrePrimaryRoutineAdapter

    private lateinit var spinnerClass: Spinner
    private lateinit var spinnerDay: Spinner
    private lateinit var gvRoutine: GridView
    private lateinit var tvTotalActivities: TextView
    private lateinit var tvSchoolHours: TextView
    private lateinit var tvCaregivers: TextView
    private lateinit var tvNapIncluded: TextView
    private lateinit var fabAddActivity: FloatingActionButton

    private var selectedClass: ClassInfo? = null
    private var selectedDay: String = "Monday"
    private val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pre_primary_timetable, container, false)

        initViews(view)
        setupViewModel()
        setupClassSpinner()
        setupDaySpinner()
        setupListeners()
        setupRoutineGrid()

        return view
    }

    private fun initViews(view: View) {
        spinnerClass = view.findViewById(R.id.spinnerPreClass)
        spinnerDay = view.findViewById(R.id.spinnerDay)
        gvRoutine = view.findViewById(R.id.gvPrePrimaryRoutine)
        tvTotalActivities = view.findViewById(R.id.tvTotalActivities)
        tvSchoolHours = view.findViewById(R.id.tvSchoolHours)
        tvCaregivers = view.findViewById(R.id.tvCaregivers)
        tvNapIncluded = view.findViewById(R.id.tvNapIncluded)
        fabAddActivity = view.findViewById(R.id.fabAddActivity)

        view.findViewById<ImageButton>(R.id.btnPreRefresh).setOnClickListener {
            loadRoutine()
        }
    }

    private fun setupViewModel() {
        val activity = requireActivity() as TimetableManagementActivity
        viewModel = ViewModelProvider(activity)[TimetableViewModel::class.java]

        viewModel.prePrimaryClasses.observe(viewLifecycleOwner) { classes ->
            if (classes.isNotEmpty()) {
                setupClassSpinner()
            }
        }

        viewModel.prePrimaryTimetable.observe(viewLifecycleOwner) { data ->
            data?.let {
                updateStats(it.stats)
                updateRoutine(it.activities)
            }
        }
    }

    private fun setupClassSpinner() {
        val classes = viewModel.prePrimaryClasses.value ?: return
        val adapter = ClassSelectorAdapter(requireContext(), classes)
        spinnerClass.adapter = adapter

        spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedClass = classes[position]
                loadRoutine()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (classes.isNotEmpty()) {
            spinnerClass.setSelection(0)
        }
    }

    private fun setupDaySpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDay.adapter = adapter

        spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDay = daysOfWeek[position]
                loadRoutine()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        fabAddActivity.setOnClickListener {
            showAddActivityDialog()
        }
    }

    private fun setupRoutineGrid() {
        routineAdapter = PrePrimaryRoutineAdapter(requireContext()) { activity ->
            showActivityOptionsDialog(activity)
        }
        gvRoutine.adapter = routineAdapter
    }

    private fun loadRoutine() {
        selectedClass?.id?.let { classId ->
            viewModel.loadPrePrimaryTimetable(classId)
        }
    }

    private fun updateStats(stats: com.st_louis.models.PrePrimaryStats) {
        tvTotalActivities.text = stats.totalActivities.toString()
        tvSchoolHours.text = stats.schoolHours
        tvCaregivers.text = stats.caregivers.toString()
        tvNapIncluded.text = if (stats.napIncluded) "Yes" else "No"
    }

    private fun updateRoutine(activities: List<PrePrimaryActivity>) {
        val filteredActivities = activities.filter { it.day == selectedDay }
        routineAdapter.updateActivities(filteredActivities)
        routineAdapter.notifyDataSetChanged()
    }

    private fun showAddActivityDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_activity, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Activity")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                Toast.makeText(requireContext(), "Activity added", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showActivityOptionsDialog(activity: PrePrimaryActivity) {
        val options = arrayOf("View Details", "Edit Activity", "Delete Activity")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Activity Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showActivityDetailsDialog(activity)
                    1 -> showEditActivityDialog(activity)
                    2 -> confirmDeleteActivity(activity)
                }
            }
            .show()
    }

    private fun showActivityDetailsDialog(activity: PrePrimaryActivity) {
        val details = """
            Activity: ${activity.activityName}
            Time: ${activity.time}
            Duration: ${activity.duration}
            Type: ${activity.type.capitalize()}
            Description: ${activity.description}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Activity Details")
            .setMessage(details)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showEditActivityDialog(activity: PrePrimaryActivity) {
        Toast.makeText(requireContext(), "Edit activity: ${activity.activityName}", Toast.LENGTH_SHORT).show()
    }

    private fun confirmDeleteActivity(activity: PrePrimaryActivity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Activity")
            .setMessage("Are you sure you want to delete ${activity.activityName}?")
            .setPositiveButton("Delete") { _, _ ->
                Toast.makeText(requireContext(), "Activity deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}