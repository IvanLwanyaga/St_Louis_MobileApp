package com.st_louis.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.st_louis.R
import com.st_louis.adapters.CalendarAdapter
import com.st_louis.adapters.UpcomingEventsAdapter
import com.st_louis.data.ApiClient
import com.st_louis.models.ScheduleEvent
import com.st_louis.ui.admin.ScheduleViewModel
import com.st_louis.ui.admin.ScheduleViewModelFactory
import com.st_louis.utils.DateUtils
import com.st_louis.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ScheduleManagementActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var eventsAdapter: UpcomingEventsAdapter

    // Views
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEventsThisMonth: TextView
    private lateinit var tvExamDays: TextView
    private lateinit var tvSchoolDaysLeft: TextView
    private lateinit var tvHolidays: TextView
    private lateinit var tvMonthYear: TextView
    private lateinit var gvCalendar: GridView
    private lateinit var rvUpcomingEvents: RecyclerView
    private lateinit var btnPrevMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton
    private lateinit var fabAddEvent: FloatingActionButton

    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_management)

        initViews()
        setupToolbar()
        setupViewModel()
        setupCalendar()
        setupRecyclerView()
        setupListeners()
        loadSchedule()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        tvEventsThisMonth = findViewById(R.id.tvEventsThisMonth)
        tvExamDays = findViewById(R.id.tvExamDays)
        tvSchoolDaysLeft = findViewById(R.id.tvSchoolDaysLeft)
        tvHolidays = findViewById(R.id.tvHolidays)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        gvCalendar = findViewById(R.id.gvCalendar)
        rvUpcomingEvents = findViewById(R.id.rvUpcomingEvents)
        btnPrevMonth = findViewById(R.id.btnPrevMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        fabAddEvent = findViewById(R.id.fabAddEvent)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Schedule Management"
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        viewModel = ViewModelProvider(
            this,
            ScheduleViewModelFactory(apiService)
        )[ScheduleViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.events.observe(this) { events ->
            updateCalendar(events)
            updateUpcomingEvents(events)
        }

        viewModel.stats.observe(this) { stats ->
            stats?.let {
                tvEventsThisMonth.text = it.eventsThisMonth.toString()
                tvExamDays.text = it.examDays.toString()
                tvSchoolDaysLeft.text = it.schoolDaysLeft.toString()
                tvHolidays.text = it.holidays.toString()
            }
        }

        viewModel.operationResult.observe(this) { result ->
            result?.let { success ->
                if (success) {
                    Toast.makeText(this, "Operation successful", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter(
            this,
            currentMonth,
            currentYear,
            emptyList()
        ) { date, events ->
            showEventDetailsDialog(date, events)
        }
        gvCalendar.adapter = calendarAdapter
        updateMonthYearDisplay()
    }

    private fun setupRecyclerView() {
        eventsAdapter = UpcomingEventsAdapter { event ->
            showEventOptionsDialog(event)
        }
        rvUpcomingEvents.layoutManager = LinearLayoutManager(this)
        rvUpcomingEvents.adapter = eventsAdapter
    }

    private fun setupListeners() {
        btnPrevMonth.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(currentYear, currentMonth - 1, 1)
            calendar.add(Calendar.MONTH, -1)
            currentMonth = calendar.get(Calendar.MONTH) + 1
            currentYear = calendar.get(Calendar.YEAR)
            updateMonthYearDisplay()
            loadSchedule()
        }

        btnNextMonth.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(currentYear, currentMonth - 1, 1)
            calendar.add(Calendar.MONTH, 1)
            currentMonth = calendar.get(Calendar.MONTH) + 1
            currentYear = calendar.get(Calendar.YEAR)
            updateMonthYearDisplay()
            loadSchedule()
        }

        fabAddEvent.setOnClickListener {
            showAddEventDialog()
        }
    }

    private fun loadSchedule() {
        viewModel.loadSchedule(currentMonth, currentYear)
    }

    private fun updateMonthYearDisplay() {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth - 1, 1)
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = dateFormat.format(calendar.time)
    }

    private fun updateCalendar(events: List<ScheduleEvent>) {
        calendarAdapter.updateEvents(events, currentMonth, currentYear)
        calendarAdapter.notifyDataSetChanged()
    }

    private fun updateUpcomingEvents(events: List<ScheduleEvent>) {
        val sortedEvents = events
            .filter { DateUtils.isDateInFuture(it.date) }
            .sortedBy { it.date }
            .take(5)
        eventsAdapter.submitList(sortedEvents)
    }

    private fun showEventDetailsDialog(date: String, events: List<ScheduleEvent>) {
        if (events.isEmpty()) {
            Toast.makeText(this, "No events on this day", Toast.LENGTH_SHORT).show()
            return
        }

        val eventNames = events.joinToString("\n") {
            "• ${it.title} (${it.eventType.capitalize()})"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Events on ${DateUtils.formatDisplayDate(date)}")
            .setMessage(eventNames)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showEventOptionsDialog(event: ScheduleEvent) {
        val options = arrayOf("View Details", "Edit Event", "Delete Event")

        MaterialAlertDialogBuilder(this)
            .setTitle("Event Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEventFullDetailsDialog(event)
                    1 -> showEditEventDialog(event)
                    2 -> confirmDeleteEvent(event)
                }
            }
            .show()
    }

    private fun showEventFullDetailsDialog(event: ScheduleEvent) {
        val details = buildString {
            append("Title: ${event.title}\n")
            append("Type: ${event.eventType.capitalize()}\n")
            append("Date: ${DateUtils.formatDisplayDate(event.date)}\n")
            event.startTime?.let { append("Start: ${DateUtils.formatDisplayTime(it)}\n") }
            event.endTime?.let { append("End: ${DateUtils.formatDisplayTime(it)}\n") }
            event.location?.let { append("Location: $it\n") }
            event.targetClasses?.let { append("Classes: $it\n") }
            if (event.isWholeSchool) {
                append("Whole School Event\n")
            }
            event.description?.let { append("\nDescription: $it") }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Event Details")
            .setMessage(details)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_event, null)
        setupEventDialog(dialogView, null)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add New Event")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val event = getEventFromDialog(dialogView)
                event?.let {
                    viewModel.createEvent(it)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditEventDialog(event: ScheduleEvent) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_event, null)
        setupEventDialog(dialogView, event)

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Event")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedEvent = getEventFromDialog(dialogView)
                updatedEvent?.let {
                    event.id?.let { id ->
                        viewModel.updateEvent(id, it)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupEventDialog(view: View, event: ScheduleEvent?) {
        val etTitle = view.findViewById<TextInputEditText>(R.id.etEventTitle)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etEventDescription)
        val etDate = view.findViewById<TextInputEditText>(R.id.etEventDate)
        val etStartTime = view.findViewById<TextInputEditText>(R.id.etStartTime)
        val etEndTime = view.findViewById<TextInputEditText>(R.id.etEndTime)
        val etLocation = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etTargetClasses = view.findViewById<TextInputEditText>(R.id.etTargetClasses)
        val spEventType = view.findViewById<Spinner>(R.id.spEventType)
        val cbWholeSchool = view.findViewById<CheckBox>(R.id.cbWholeSchool)

        // Setup event type spinner
        val eventTypes = arrayOf("sports", "exams", "special", "holiday")
        val displayTypes = arrayOf("Sports", "Exams", "Special", "Holiday")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEventType.adapter = adapter

        // Setup date picker
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDate.setText(date)
            }, year, month, day).show()
        }

        // Setup time pickers
        etStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, hourOfDay, minuteOfHour ->
                val time = String.format("%02d:%02d", hourOfDay, minuteOfHour)
                etStartTime.setText(time)
            }, hour, minute, true).show()
        }

        etEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, hourOfDay, minuteOfHour ->
                val time = String.format("%02d:%02d", hourOfDay, minuteOfHour)
                etEndTime.setText(time)
            }, hour, minute, true).show()
        }

        // Populate if editing
        event?.let {
            etTitle.setText(it.title)
            etDescription.setText(it.description)
            etDate.setText(it.date)
            etStartTime.setText(it.startTime)
            etEndTime.setText(it.endTime)
            etLocation.setText(it.location)
            etTargetClasses.setText(it.targetClasses)
            val position = eventTypes.indexOf(it.eventType)
            if (position >= 0) spEventType.setSelection(position)
            cbWholeSchool.isChecked = it.isWholeSchool
        }
    }

    private fun getEventFromDialog(view: View): ScheduleEvent? {
        val etTitle = view.findViewById<TextInputEditText>(R.id.etEventTitle)
        val etDescription = view.findViewById<TextInputEditText>(R.id.etEventDescription)
        val etDate = view.findViewById<TextInputEditText>(R.id.etEventDate)
        val etStartTime = view.findViewById<TextInputEditText>(R.id.etStartTime)
        val etEndTime = view.findViewById<TextInputEditText>(R.id.etEndTime)
        val etLocation = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etTargetClasses = view.findViewById<TextInputEditText>(R.id.etTargetClasses)
        val spEventType = view.findViewById<Spinner>(R.id.spEventType)
        val cbWholeSchool = view.findViewById<CheckBox>(R.id.cbWholeSchool)

        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            return null
        }

        val date = etDate.text.toString().trim()
        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return null
        }

        val eventTypes = arrayOf("sports", "exams", "special", "holiday")
        val selectedType = eventTypes[spEventType.selectedItemPosition]

        return ScheduleEvent(
            title = title,
            description = etDescription.text.toString().trim().takeIf { it.isNotEmpty() },
            date = date,
            startTime = etStartTime.text.toString().trim().takeIf { it.isNotEmpty() },
            endTime = etEndTime.text.toString().trim().takeIf { it.isNotEmpty() },
            eventType = selectedType,
            location = etLocation.text.toString().trim().takeIf { it.isNotEmpty() },
            targetClasses = etTargetClasses.text.toString().trim().takeIf { it.isNotEmpty() },
            isWholeSchool = cbWholeSchool.isChecked
        )
    }

    private fun confirmDeleteEvent(event: ScheduleEvent) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete \"${event.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                event.id?.let {
                    viewModel.deleteEvent(it)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_export -> {
                Toast.makeText(this, "Exporting schedule...", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_refresh -> {
                loadSchedule()
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val filters = arrayOf("All Events", "Sports", "Exams", "Special", "Holiday")
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Events")
            .setItems(filters) { _, which ->
                when (which) {
                    0 -> viewModel.loadSchedule(currentMonth, currentYear)
                    else -> {
                        val type = filters[which].lowercase()
                        viewModel.filterEvents(type)
                    }
                }
            }
            .show()
    }
}