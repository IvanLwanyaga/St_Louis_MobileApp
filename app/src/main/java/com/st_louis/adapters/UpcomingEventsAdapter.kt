package com.st_louis.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.st_louis.R
import com.st_louis.models.ScheduleEvent
import com.st_louis.utils.DateUtils

class UpcomingEventsAdapter(
    private val onEventClick: (ScheduleEvent) -> Unit
) : RecyclerView.Adapter<UpcomingEventsAdapter.EventViewHolder>() {

    private var events: List<ScheduleEvent> = emptyList()

    fun submitList(newEvents: List<ScheduleEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_events, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewEventColor = itemView.findViewById<View>(R.id.viewEventColor)
        private val tvEventTitle = itemView.findViewById<TextView>(R.id.tvEventTitle)
        private val tvEventDate = itemView.findViewById<TextView>(R.id.tvEventDate)
        private val tvEventDetails = itemView.findViewById<TextView>(R.id.tvEventDetails)
        private val tvEventType = itemView.findViewById<TextView>(R.id.tvEventType)

        fun bind(event: ScheduleEvent) {
            tvEventTitle.text = event.title
            tvEventDate.text = DateUtils.formatDisplayDate(event.date)

            val details = buildString {
                event.startTime?.let { append(DateUtils.formatDisplayTime(it)) }
                event.endTime?.let {
                    if (isNotEmpty()) append(" - ")
                    append(DateUtils.formatDisplayTime(it))
                }
                event.location?.let {
                    if (isNotEmpty()) append(" · ")
                    append(it)
                }
            }
            tvEventDetails.text = details.ifEmpty { "No details" }

            tvEventType.text = event.eventType.capitalize()
            tvEventType.setBackgroundColor(getEventColor(event.eventType))
            viewEventColor.setBackgroundColor(getEventColor(event.eventType))

            itemView.setOnClickListener {
                onEventClick(event)
            }
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
}