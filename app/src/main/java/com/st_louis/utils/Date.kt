package com.st_louis.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    private const val DISPLAY_TIME_FORMAT = "hh:mm a"

    fun formatDisplayDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val dateObj = inputFormat.parse(date)
            outputFormat.format(dateObj ?: Date())
        } catch (e: Exception) {
            date
        }
    }

    fun formatDisplayTime(time: String?): String {
        if (time.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_TIME_FORMAT, Locale.getDefault())
            val timeObj = inputFormat.parse(time)
            outputFormat.format(timeObj ?: Date())
        } catch (e: Exception) {
            time
        }
    }

    fun isDateInFuture(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val eventDate = dateFormat.parse(date)
            val today = dateFormat.format(Date())
            val todayDate = dateFormat.parse(today)
            eventDate?.after(todayDate) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    }
}