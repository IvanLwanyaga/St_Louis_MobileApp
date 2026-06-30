package com.st_louis.data

import com.st_louis.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    // ==================== DASHBOARD STATS ====================

    @GET("stats/admin")
    suspend fun getAdminStats(): Response<AdminStats>

    @GET("stats/student/{id}")
    suspend fun getStudentStats(@Path("id") id: String): Response<StudentStats>

    @GET("stats/teacher/{id}")
    suspend fun getTeacherStats(@Path("id") id: String): Response<TeacherStats>

    @GET("stats/parent/{id}")
    suspend fun getParentStats(@Path("id") id: String): Response<ParentStats>

    @GET("stats/bursar")
    suspend fun getBursarStats(): Response<BursarStats>

    // ==================== STUDENT ENDPOINTS ====================

    @GET("students")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("students/{id}")
    suspend fun getStudent(@Path("id") id: String): Response<Student>

    // ==================== FEE ENDPOINTS ====================

    @GET("student/{studentId}/fees/details")
    suspend fun getFeeDetails(@Path("studentId") studentId: String): Response<FeeDetailsResponse>

    // ==================== EXAM RESULTS ENDPOINTS ====================

    @GET("student/{studentId}/exam-periods")
    suspend fun getExamPeriods(@Path("studentId") studentId: String): Response<List<ExamPeriod>>

    @GET("student/{studentId}/results/{periodId}")
    suspend fun getAcademicResults(
        @Path("studentId") studentId: String,
        @Path("periodId") periodId: String
    ): Response<AcademicResultsResponse>

    // ==================== ATTENDANCE ENDPOINTS ====================

    @GET("teacher/{teacherId}/attendance/today")
    suspend fun getTodayAttendance(@Path("teacherId") teacherId: String): Response<AttendanceSummary>

    @GET("teacher/{teacherId}/attendance/student/{studentId}")
    suspend fun getStudentAttendance(
        @Path("teacherId") teacherId: String,
        @Path("studentId") studentId: String
    ): Response<StudentAttendance>

    @POST("attendance/mark")
    suspend fun markAttendance(@Body attendanceData: Map<String, Any>): Response<Unit>

    // ==================== TEACHER CLASSES ====================

    @GET("teacher/{teacherId}/classes")
    suspend fun getTeacherClasses(@Path("teacherId") teacherId: String): Response<List<TeacherClass>>

    @GET("teacher/{teacherId}/class/{classId}/students")
    suspend fun getClassStudents(
        @Path("teacherId") teacherId: String,
        @Path("classId") classId: String
    ): Response<List<Student>>
}
