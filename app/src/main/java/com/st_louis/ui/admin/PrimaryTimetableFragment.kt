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
import java.text.SimpleDateFormat
import java.util.*

class PrimaryTimetable(
    private val context: Context,
    private val onPeriodClick: (TimetablePeriod) -> Unit
) : BaseAdapter() {

    private var periods: List<TimetablePeriod> = emptyList()
    private var viewMode: String = "week"
    private var daysOfWeek: Array<String> = arrayOf()
    private var timeSlots: List<String> = listOf()
    private val inflater = LayoutInflater.from(context)

    fun updatePeriods(newPeriods: List<TimetablePeriod>, mode: String, days: Array<String>, times: List<String>) {
        this.periods = newPeriods
        this.viewMode = mode
        this.daysOfWeek = days
        this.timeSlots = times
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (viewMode == "week") {
            (timeSlots.size + 1) * (daysOfWeek.size + 1)
        } else {
            timeSlots.size + 1
        }
    }

    override fun getItem(position: Int): Any = position

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_timetable_period, parent, false)

        val tvContent = view.findViewById<TextView>(R.id.tvPeriodContent)
        val tvSubContent = view.findViewById<TextView>(R.id.tvPeriodSubContent)
        val llContainer = view.findViewById<LinearLayout>(R.id.llPeriodContainer)

        // Reset view
        tvContent.text = ""
        tvSubContent.visibility = View.GONE
        llContainer.setBackgroundColor(Color.TRANSPARENT)
        view.setOnClickListener(null)

        val totalColumns = if (viewMode == "week") daysOfWeek.size + 1 else 2
        val row = position / totalColumns
        val col = position % totalColumns

        // Header row
        if (row == 0) {
            if (col == 0) {
                tvContent.text = "Time"
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

                val color = getSubjectColor(period.subject)
                llContainer.setBackgroundColor(color)

                if (period.hasConflict) {
                    llContainer.setBackgroundColor(Color.parseColor("#FFEBEE"))
                    tvContent.setTextColor(Color.RED)
                } else {
                    tvContent.setTextColor(Color.parseColor("#212121"))
                }

                view.setOnClickListener { onPeriodClick(period) }
            }
        }

        return view
    }

    private fun getDateForDay(dayIndex: Int): String {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val targetDay = when (daysOfWeek[dayIndex]) {
            "Monday" -> Calendar.MONDAY
            "Tuesday" -> Calendar.TUESDAY
            "Wednesday" -> Calendar.WEDNESDAY
            "Thursday" -> Calendar.THURSDAY
            "Friday" -> Calendar.FRIDAY
            else -> Calendar.MONDAY
        }

        val diff = targetDay - currentDay
        calendar.add(Calendar.DAY_OF_WEEK, diff)

        return SimpleDateFormat("MMM d", Locale.getDefault()).format(calendar.time)
    }

    private fun getSubjectColor(subject: String): Int {
        return when (subject.lowercase()) {
            "mathematics", "maths", "math" -> Color.parseColor("#E3F2FD")
            "english" -> Color.parseColor("#F3E5F5")
            "science" -> Color.parseColor("#E8F5E9")
            "sst", "social studies" -> Color.parseColor("#FFF3E0")
            "p.e.", "pe", "physical education" -> Color.parseColor("#FCE4EC")
            "r.e.", "re", "religious education" -> Color.parseColor("#E0F7FA")
            "ict" -> Color.parseColor("#F1F8E9")
            "creative arts" -> Color.parseColor("#FFF8E1")
            else -> Color.parseColor("#FAFAFA")
        }
    }
}