package com.st_louis.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.st_louis.R
import com.st_louis.models.ScheduleEvent
import java.util.*

class CalendarAdapter(
    private val context: Context,
    private var currentMonth: Int,
    private var currentYear: Int,
    private var events: List<ScheduleEvent>,
    private val onDayClick: (String, List<ScheduleEvent>) -> Unit
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var days = mutableListOf<String>()
    private val today = Calendar.getInstance()
    private val calendar = Calendar.getInstance()
    private val TAG = "CalendarAdapter"

    init {
        Log.d(TAG, "Initializing CalendarAdapter with month: $currentMonth, year: $currentYear")
        generateDays()
        Log.d(TAG, "Generated ${days.size} days")
    }

    private fun generateDays() {
        days.clear()

        // Set calendar to first day of month
        calendar.set(currentYear, currentMonth - 1, 1)

        // Get first day of month (1=Sunday, 2=Monday, etc.)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

        // Get days in month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Calculate empty days before first day
        val emptyDays = if (firstDayOfMonth == Calendar.SUNDAY) {
            0
        } else {
            firstDayOfMonth - 1
        }

        Log.d(TAG, "First day of month: $firstDayOfMonth, Empty days: $emptyDays, Days in month: $daysInMonth")

        // Add empty days
        for (i in 0 until emptyDays) {
            days.add("")
        }

        // Add all days of the month
        for (day in 1..daysInMonth) {
            days.add(day.toString())
        }

        Log.d(TAG, "Total days after generation: ${days.size}")
    }

    fun updateEvents(newEvents: List<ScheduleEvent>, month: Int, year: Int) {
        Log.d(TAG, "Updating events: ${newEvents.size} events for $month/$year")
        this.events = newEvents
        this.currentMonth = month
        this.currentYear = year
        generateDays()
        notifyDataSetChanged()
        Log.d(TAG, "Adapter updated with ${days.size} days")
    }

    override fun getCount(): Int {
        Log.d(TAG, "getCount: ${days.size}")
        return days.size
    }

    override fun getItem(position: Int): String = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_calendar_day, parent, false)

        val tvDayNumber = view.findViewById<TextView>(R.id.tvDayNumber)
        val viewIndicator1 = view.findViewById<View>(R.id.viewEventIndicator1)
        val viewIndicator2 = view.findViewById<View>(R.id.viewEventIndicator2)
        val viewIndicator3 = view.findViewById<View>(R.id.viewEventIndicator3)

        val day = days[position]
        tvDayNumber.text = day

        // Reset indicators
        viewIndicator1.visibility = View.GONE
        viewIndicator2.visibility = View.GONE
        viewIndicator3.visibility = View.GONE

        // Reset background
        tvDayNumber.setBackgroundResource(0)
        tvDayNumber.setTextColor(Color.parseColor("#212121"))

        if (day.isNotEmpty()) {
            val dayInt = day.toInt()
            val date = String.format("%d-%02d-%02d", currentYear, currentMonth, dayInt)

            // Check if it's today
            if (today.get(Calendar.DAY_OF_MONTH) == dayInt &&
                today.get(Calendar.MONTH) + 1 == currentMonth &&
                today.get(Calendar.YEAR) == currentYear) {
                tvDayNumber.setBackgroundResource(R.drawable.today_circle)
                tvDayNumber.setTextColor(Color.WHITE)
            }

            // Get events for this day
            val dayEvents = events.filter { it.date == date }

            if (dayEvents.isNotEmpty()) {
                val indicators = listOf(viewIndicator1, viewIndicator2, viewIndicator3)
                dayEvents.take(3).forEachIndexed { index, event ->
                    if (index < indicators.size) {
                        indicators[index].visibility = View.VISIBLE
                        indicators[index].setBackgroundColor(getEventColor(event.eventType))
                    }
                }
            }

            // Set click listener
            view.setOnClickListener {
                onDayClick(date, dayEvents)
            }

            view.isEnabled = true
        } else {
            // Empty days
            tvDayNumber.setTextColor(Color.parseColor("#BDBDBD"))
            view.setOnClickListener(null)
            view.isEnabled = false
        }

        return view
    }

    private fun getEventColor(eventType: String): Int {
        return when (eventType.lowercase()) {
            "sports" -> Color.parseColor("#4CAF50")
            "exams" -> Color.parseColor("#F44336")
            "special" -> Color.parseColor("#9C27B0")
            "holiday" -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#2196F3")
        }
    }
}