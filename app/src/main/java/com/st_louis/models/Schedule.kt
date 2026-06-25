package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class ScheduleEvent(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("date")
    val date: String, // Format: "YYYY-MM-DD"

    @SerializedName("start_time")
    val startTime: String? = null,

    @SerializedName("end_time")
    val endTime: String? = null,

    @SerializedName("event_type")
    val eventType: String, // "sports", "exams", "special", "holiday"

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("target_classes")
    val targetClasses: String? = null,

    @SerializedName("is_whole_school")
    val isWholeSchool: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("created_by")
    val createdBy: Int? = null,

    @SerializedName("color_code")
    val colorCode: String? = null
)

data class ScheduleStats(
    @SerializedName("events_this_month")
    val eventsThisMonth: Int,

    @SerializedName("exam_days")
    val examDays: Int,

    @SerializedName("school_days_left")
    val schoolDaysLeft: Int,

    @SerializedName("holidays")
    val holidays: Int,

    @SerializedName("total_events")
    val totalEvents: Int? = null,

    @SerializedName("upcoming_events")
    val upcomingEvents: Int? = null
)

data class ScheduleResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: ScheduleData? = null,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null
)

data class ScheduleData(
    @SerializedName("events")
    val events: List<ScheduleEvent>,

    @SerializedName("stats")
    val stats: ScheduleStats,

    @SerializedName("month")
    val month: Int? = null,

    @SerializedName("year")
    val year: Int? = null
)