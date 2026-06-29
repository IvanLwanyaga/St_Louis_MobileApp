package com.st_louis.navigation

import android.content.Context
import android.content.Intent
import com.st_louis.LoginActivity
import com.st_louis.ui.admin.AdminDashboardActivity
import com.st_louis.ui.admin.ManageUsersActivity
import com.st_louis.ui.admin.StudentManagementActivity
import com.st_louis.ui.admin.TeacherManagementActivity
import com.st_louis.ui.admin.BursarManagementActivity
import com.st_louis.ui.admin.TimetableManagementActivity
import com.st_louis.ui.admin.FinanceManagementActivity
import com.st_louis.ui.admin.ReportsActivity
import com.st_louis.ui.admin.SettingsActivity
import com.st_louis.ui.bursar.BursarDashboardActivity
import com.st_louis.ui.parent.ParentDashboardActivity
import com.st_louis.ui.student.StudentDashboardActivity
import com.st_louis.ui.teacher.TeacherDashboardActivity

object NavigationRoutes {

    fun navigateToLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun navigateToAdminDashboard(context: Context) {
        context.startActivity(Intent(context, AdminDashboardActivity::class.java))
    }

    fun navigateToTeacherDashboard(context: Context) {
        context.startActivity(Intent(context, TeacherDashboardActivity::class.java))
    }

    fun navigateToBursarDashboard(context: Context) {
        context.startActivity(Intent(context, BursarDashboardActivity::class.java))
    }

    fun navigateToStudentDashboard(context: Context) {
        context.startActivity(Intent(context, StudentDashboardActivity::class.java))
    }

    fun navigateToParentDashboard(context: Context) {
        context.startActivity(Intent(context, ParentDashboardActivity::class.java))
    }

    fun navigateToStudentManagement(context: Context) {
        context.startActivity(Intent(context, StudentManagementActivity::class.java))
    }

    fun navigateToTeacherManagement(context: Context) {
        context.startActivity(Intent(context, TeacherManagementActivity::class.java))
    }

    fun navigateToBursarManagement(context: Context) {
        context.startActivity(Intent(context, BursarManagementActivity::class.java))
    }

    fun navigateToTimetable(context: Context) {
        context.startActivity(Intent(context, TimetableManagementActivity::class.java))
    }

    fun navigateToFinance(context: Context) {
        context.startActivity(Intent(context, FinanceManagementActivity::class.java))
    }

    fun navigateToReports(context: Context) {
        context.startActivity(Intent(context, ReportsActivity::class.java))
    }

    fun navigateToSettings(context: Context) {
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    fun navigateToAttendance(context: Context) {
        val intent = Intent(context, com.st_louis.ui.attendance.AttendanceActivity::class.java)
        context.startActivity(intent)
    }

    fun navigateToRegistration(context: Context, userType: String) {
        val intent = Intent(context, ManageUsersActivity::class.java)
        intent.putExtra("USER_TYPE", userType)
        context.startActivity(intent)
    }
}
