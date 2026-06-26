package com.st_louis.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.st_louis.R
import com.st_louis.models.TimetablePeriod

class PrimaryTimetableAdapter(
    private val context: Context,
    private val onPeriodClick: (TimetablePeriod) -> Unit
) : BaseAdapter() {

    private var periods: List<TimetablePeriod> = emptyList()
    private var viewMode: String = "week"
    private var daysOfWeek: Array<String> = arrayOf()
    private val timeSlots = listOf("7:30", "8:30", "9:30", "10:30", "11:30")
    private val inflater = LayoutInflater.from(context)

    fun updatePeriods(newPeriods: List<TimetablePeriod>, mode: String, days: Array<String>) {
        this.periods = newPeriods
        this.viewMode = mode
        this.daysOfWeek = days
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (viewMode == "week") {
            (timeSlots.size + 1) * (daysOfWeek.size + 1) // Header row + time slots
        } else {
            timeSlots.size + 1 // Header row + time slots for single day
        }
    }

    override fun getItem(position: Int): Any = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_timetable_period, parent, false)

        val tvContent = view.findViewById<TextView>(R.id.tvPeriodContent)
        val tvSubContent = view.findViewById<TextView>(R.id.tvPeriodSubContent)
        val llContainer = view.findViewById<LinearLayout>(R.id.llPeriodContainer)

        val totalColumns = if (viewMode == "week") daysOfWeek.size + 1 else 2
        val row = position / totalColumns
        val col = position % totalColumns

        // Header row
        if (row == 0) {
            if (col == 0) {
                tvContent.text = "Time"
                tvSubContent.visibility = View.GONE
                llContainer.setBackgroundColor(Color.parseColor("#E3F2FD"))
            } else {
                val dayIndex = col - 1
                if (dayIndex < daysOfWeek.size) {
                    tvContent.text = daysOfWeek[dayIndex]
                    tvSubContent.text = getDateForDay(dayIndex)
                    tvSubContent.visibility = View.VISIBLE
                    llContainer.setBackgroundColor(Color.parseColor("#E3F2FD"))
                }
            }
            return view
        }

        // Time slot column
        if (col == 0) {
            val timeIndex = row - 1
            if (timeIndex < timeSlots.size) {
                tvContent.text = timeSlots[timeIndex]
                tvSubContent.visibility = View.GONE
                llContainer.setBackgroundColor(Color.parseColor("#F5F5F5"))
            }
            return view
        }

        // Period content
        val dayIndex = col - 1
        val timeIndex = row - 1

        if (dayIndex < daysOfWeek.size && timeIndex < timeSlots.size) {
            val day = daysOfWeek[dayIndex]
            val time = timeSlots[timeIndex]

            val period = periods.find { it.day == day && it.time == time }

            if (period != null) {
                tvContent.text = period.subject
                tvSubContent.text = "${period.teacher}\n${period.room}"
                tvSubContent.visibility = View.VISIBLE

                // Set color based on subject
                val color = getSubjectColor(period.subject)
                llContainer.setBackgroundColor(color)

                // Highlight conflicts
                if (period.hasConflict) {
                    llContainer.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    tvContent.setTextColor(Color.RED)
                }

                view.setOnClickListener { onPeriodClick(period) }
            } else {
                tvContent.text = ""
                tvSubContent.visibility = View.GONE
                llContainer.setBackgroundColor(Color.TRANSPARENT)
                view.setOnClickListener(null)
            }
        }

        return view
    }

    private fun getDateForDay(dayIndex: Int): String {
        // Calculate actual date for the day
        val calendar = java.util.Calendar.getInstance()
        val currentDay = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val targetDay = when (daysOfWeek[dayIndex]) {
            "Monday" -> java.util.Calendar.MONDAY
            "Tuesday" -> java.util.Calendar.TUESDAY
            "Wednesday" -> java.util.Calendar.WEDNESDAY
            "Thursday" -> java.util.Calendar.THURSDAY
            "Friday" -> java.util.Calendar.FRIDAY
            else -> java.util.Calendar.MONDAY
        }

        val diff = targetDay - currentDay
        calendar.add(java.util.Calendar.DAY_OF_WEEK, diff)

        return java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault()).format(calendar.time)
    }

    private fun getSubjectColor(subject: String): Int {
        return when (subject.lowercase()) {
            "mathematics" -> Color.parseColor("#E3F2FD")
            "english" -> Color.parseColor("#F3E5F5")
            "science" -> Color.parseColor("#E8F5E9")
            "sst" -> Color.parseColor("#FFF3E0")
            "p.e." -> Color.parseColor("#FCE4EC")
            "r.e." -> Color.parseColor("#E0F7FA")
            "ict" -> Color.parseColor("#F1F8E9")
            else -> Color.parseColor("#FAFAFA")
        }
    }
}