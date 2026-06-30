package com.st_louis.models

import com.google.gson.annotations.SerializedName

data class AttendanceStats(
    @SerializedName("today")
    val today: DailyAttendanceStats,

    @SerializedName("week")
    val week: WeeklyAttendanceStats,

    @SerializedName("month")
    val month: MonthlyAttendanceStats,

    @SerializedName("overall")
    val overall: OverallAttendanceStats
)

data class DailyAttendanceStats(
    @SerializedName("date")
    val date: String,

    @SerializedName("total_students")
    val totalStudents: Int,

    @SerializedName("present")
    val present: Int,

    @SerializedName("absent")
    val absent: Int,

    @SerializedName("late")
    val late: Int,

    @SerializedName("excused")
    val excused: Int,

    @SerializedName("not_marked")
    val notMarked: Int,

    @SerializedName("attendance_rate")
    val attendanceRate: Double
)

data class WeeklyAttendanceStats(
    @SerializedName("week_start")
    val weekStart: String,

    @SerializedName("week_end")
    val weekEnd: String,

    @SerializedName("average_rate")
    val averageRate: Double,

    @SerializedName("days")
    val days: List<DailyAttendanceStats>
)

data class MonthlyAttendanceStats(
    @SerializedName("month")
    val month: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("average_rate")
    val averageRate: Double,

    @SerializedName("total_days")
    val totalDays: Int,

    @SerializedName("days")
    val days: List<DailyAttendanceStats>
)

data class OverallAttendanceStats(
    @SerializedName("total_days")
    val totalDays: Int,

    @SerializedName("average_rate")
    val averageRate: Double,

    @SerializedName("best_month")
    val bestMonth: String? = null,

    @SerializedName("worst_month")
    val worstMonth: String? = null
)