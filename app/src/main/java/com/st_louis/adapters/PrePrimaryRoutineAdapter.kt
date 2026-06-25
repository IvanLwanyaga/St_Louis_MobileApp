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
import com.st_louis.models.PrePrimaryActivity

class PrePrimaryRoutineAdapter(
    private val context: Context,
    private val onActivityClick: (PrePrimaryActivity) -> Unit
) : BaseAdapter() {

    private var activities: List<PrePrimaryActivity> = emptyList()
    private val inflater = LayoutInflater.from(context)

    fun updateActivities(newActivities: List<PrePrimaryActivity>) {
        this.activities = newActivities
        notifyDataSetChanged()
    }

    override fun getCount(): Int = activities.size + 1 // +1 for header

    override fun getItem(position: Int): Any {
        return if (position == 0) "header" else activities[position - 1]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_timetable_period, parent, false)

        val tvContent = view.findViewById<TextView>(R.id.tvPeriodContent)
        val tvSubContent = view.findViewById<TextView>(R.id.tvPeriodSubContent)
        val llContainer = view.findViewById<LinearLayout>(R.id.llPeriodContainer)

        if (position == 0) {
            // Header row
            tvContent.text = "Time"
            tvSubContent.text = "Activity"
            tvSubContent.visibility = View.VISIBLE
            llContainer.setBackgroundColor(Color.parseColor("#E3F2FD"))
            return view
        }

        val activity = activities[position - 1]
        tvContent.text = activity.time
        tvSubContent.text = "${activity.activityName}\n${activity.description}"
        tvSubContent.visibility = View.VISIBLE

        // Set color based on activity type
        val color = getActivityTypeColor(activity.type)
        llContainer.setBackgroundColor(color)

        view.setOnClickListener { onActivityClick(activity) }

        return view
    }

    private fun getActivityTypeColor(type: String): Int {
        return when (type.lowercase()) {
            "group" -> Color.parseColor("#E3F2FD")
            "individual" -> Color.parseColor("#F3E5F5")
            "class" -> Color.parseColor("#E8F5E9")
            else -> Color.parseColor("#FAFAFA")
        }
    }
}