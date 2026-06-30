package com.st_louis.data.repository

import com.st_louis.data.ApiService
import com.st_louis.models.ApiResponse
import com.st_louis.models.AttendanceData
import com.st_louis.models.AttendanceExport
import com.st_louis.models.AttendanceRecord
import com.st_louis.models.AttendanceRequest
import com.st_louis.models.AttendanceStats
import com.st_louis.models.AttendanceSummary
import com.st_louis.models.SimpleApiResponse
import com.st_louis.models.Student
import com.st_louis.models.StudentAttendance
import com.st_louis.models.TeacherClass
import retrofit2.Response

class AttendanceRepository(
    private val apiService: ApiService
) {

    // ==================== CLASSES ====================

    suspend fun getTeacherClasses(teacherId: String): Response<List<TeacherClass>> {
        return apiService.getTeacherClasses(teacherId)
    }

    suspend fun getStudentsByClass(teacherId: String, classId: String): Response<List<Student>> {
        return apiService.getClassStudents(teacherId, classId)
    }

    // ==================== SUBMIT ATTENDANCE ====================

    suspend fun submitAttendance(attendanceData: Map<String, Any>): Response<ApiResponse<AttendanceData>> {
        val request = AttendanceRequest(
            teacherId = attendanceData["teacher_id"] as String,
            classId = attendanceData["class_id"] as String,
            className = attendanceData["class_name"] as? String ?: "",
            date = attendanceData["date"] as String,
            section = attendanceData["section"] as String,
            attendance = (attendanceData["attendance"] as List<Map<String, Any>>).map {
                AttendanceRecord(
                    studentId = it["student_id"] as String,
                    studentName = it["student_name"] as? String,
                    admissionNo = it["admission_no"] as? String,
                    present = it["present"] as Boolean,
                    status = it["status"] as? String,
                    remarks = it["remarks"] as? String,
                    timeIn = it["time_in"] as? String,
                    timeOut = it["time_out"] as? String
                )
            }
        )
        return apiService.markAttendance(request)
    }

    // ==================== GET ATTENDANCE ====================

    suspend fun getTodayAttendance(teacherId: String): Response<AttendanceSummary> {
        return apiService.getTodayAttendance(teacherId)
    }

    suspend fun getAttendanceByDate(
        teacherId: String,
        date: String
    ): Response<AttendanceSummary> {
        return apiService.getAttendanceByDate(teacherId, date)
    }

    suspend fun getClassAttendance(
        teacherId: String,
        classId: String,
        date: String? = null
    ): Response<AttendanceSummary> {
        return apiService.getClassAttendance(teacherId, classId, date)
    }

    suspend fun getStudentAttendance(
        teacherId: String,
        studentId: String,
        from: String? = null,
        to: String? = null
    ): Response<StudentAttendance> {
        return apiService.getStudentAttendance(teacherId, studentId, from, to)
    }

    suspend fun getAttendanceStats(
        teacherId: String,
        classId: String? = null,
        month: Int? = null,
        year: Int? = null
    ): Response<AttendanceStats> {
        return apiService.getAttendanceStats(teacherId, classId, month, year)
    }

    // ==================== UPDATE ATTENDANCE ====================

    suspend fun updateAttendance(
        attendanceId: String,
        attendanceData: Map<String, Any>
    ): Response<ApiResponse<AttendanceData>> {
        val request = AttendanceRequest(
            teacherId = attendanceData["teacher_id"] as String,
            classId = attendanceData["class_id"] as String,
            className = attendanceData["class_name"] as? String ?: "",
            date = attendanceData["date"] as String,
            section = attendanceData["section"] as String,
            attendance = (attendanceData["attendance"] as List<Map<String, Any>>).map {
                AttendanceRecord(
                    studentId = it["student_id"] as String,
                    studentName = it["student_name"] as? String,
                    admissionNo = it["admission_no"] as? String,
                    present = it["present"] as Boolean,
                    status = it["status"] as? String,
                    remarks = it["remarks"] as? String,
                    timeIn = it["time_in"] as? String,
                    timeOut = it["time_out"] as? String
                )
            }
        )
        return apiService.updateAttendance(attendanceId, request)
    }

    suspend fun updateStudentAttendance(
        studentId: String,
        record: AttendanceRecord
    ): Response<ApiResponse<AttendanceData>> {
        return apiService.updateStudentAttendance(studentId, record)
    }

    // ==================== DELETE ATTENDANCE ====================

    suspend fun deleteAttendance(attendanceId: String): Response<SimpleApiResponse> {
        return apiService.deleteAttendance(attendanceId)
    }

    // ==================== EXPORT ATTENDANCE ====================

    suspend fun exportAttendance(
        teacherId: String,
        classId: String? = null,
        from: String? = null,
        to: String? = null,
        format: String = "pdf"
    ): Response<AttendanceExport> {
        return apiService.exportAttendance(teacherId, classId, from, to, format)
    }
}