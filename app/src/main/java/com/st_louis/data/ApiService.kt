package com.st_louis.data

import com.st_louis.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @POST("auth/login")
    suspend fun loginWithMap(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Map<String, String>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Map<String, String>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Header("Authorization") token: String): Response<RefreshTokenResponse>

    // ==================== STUDENT ENDPOINTS ====================

    @GET("students")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("students/{id}")
    suspend fun getStudent(@Path("id") id: String): Response<Student>

    @POST("students")
    suspend fun registerStudent(@Body student: Student): Response<Student>

    @POST("students/register")
    suspend fun registerStudentAlt(@Body student: Student): Response<Student>

    @PUT("students/{id}")
    suspend fun updateStudent(@Path("id") id: String, @Body student: Student): Response<Student>

    @DELETE("students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): Response<Unit>

    @GET("students/level/{level}")
    suspend fun getStudentsByLevel(@Path("level") level: String): Response<List<Student>>

    @GET("students/class/{className}")
    suspend fun getStudentsByClass(@Path("className") className: String): Response<List<Student>>

    @GET("students/parent/{email}")
    suspend fun getStudentsByParentEmail(@Path("email") email: String): Response<List<Student>>

    @GET("students/check-admission/{admissionNumber}")
    suspend fun checkAdmissionNumber(@Path("admissionNumber") admissionNumber: String): Response<BooleanResponse>

    @GET("students/check-username/{username}")
    suspend fun checkUsername(@Path("username") username: String): Response<BooleanResponse>

    // ==================== USER ENDPOINTS ====================

    @GET("users")
    suspend fun getAllUsers(): Response<List<UserAccount>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserAccount>

    @GET("users/role/{role}")
    suspend fun getUsersByRole(@Path("role") role: String): Response<List<UserAccount>>

    @POST("users")
    suspend fun createUser(@Body user: UserAccount): Response<UserAccount>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserAccount): Response<UserAccount>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    // ==================== DASHBOARD STATS ====================

    @GET("stats/admin")
    suspend fun getAdminStats(): Response<AdminStats>

    @GET("stats/student/{id}")
    suspend fun getStudentStats(@Path("id") id: String): Response<StudentStats>

    @GET("stats/teacher/{id}")
    suspend fun getTeacherStats(@Path("id") id: String): Response<TeacherStats>

    @GET("stats/bursar")
    suspend fun getBursarStats(): Response<BursarStats>

    @GET("stats/parent/{id}")
    suspend fun getParentStats(@Path("id") id: String): Response<ParentStats>

    // ==================== SCHEDULE MANAGEMENT ENDPOINTS ====================

    @GET("admin/schedule")
    suspend fun getSchedule(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ScheduleResponse>

    @GET("admin/schedule/events")
    suspend fun getEvents(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("event_type") eventType: String? = null
    ): Response<List<ScheduleEvent>>

    @GET("admin/schedule/events/{id}")
    suspend fun getEventById(@Path("id") id: Int): Response<ScheduleEvent>

    @POST("admin/schedule/events")
    suspend fun createEvent(@Body event: ScheduleEvent): Response<ScheduleResponse>

    @PUT("admin/schedule/events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Body event: ScheduleEvent
    ): Response<ScheduleResponse>

    @DELETE("admin/schedule/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int): Response<ScheduleResponse>

    @GET("admin/schedule/stats")
    suspend fun getScheduleStats(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ScheduleStats>

    @GET("admin/schedule/upcoming")
    suspend fun getUpcomingEvents(
        @Query("limit") limit: Int = 10,
        @Query("days") days: Int = 30
    ): Response<List<ScheduleEvent>>

    @GET("admin/schedule/events/date/{date}")
    suspend fun getEventsByDate(@Path("date") date: String): Response<List<ScheduleEvent>>

    @GET("admin/schedule/events/type/{eventType}")
    suspend fun getEventsByType(@Path("eventType") eventType: String): Response<List<ScheduleEvent>>

    @GET("admin/schedule/search")
    suspend fun searchEvents(@Query("q") query: String): Response<List<ScheduleEvent>>

    @POST("admin/schedule/events/bulk")
    suspend fun createBulkEvents(@Body events: List<ScheduleEvent>): Response<ScheduleResponse>

    @DELETE("admin/schedule/events/date/{date}")
    suspend fun deleteEventsByDate(@Path("date") date: String): Response<ScheduleResponse>

    // ==================== TIMETABLE MANAGEMENT ====================

    // --- Primary Timetable ---
    @GET("admin/timetable/primary/classes")
    suspend fun getPrimaryClasses(): Response<List<ClassInfo>>

    @GET("admin/timetable/primary/{classId}")
    suspend fun getPrimaryTimetable(
        @Path("classId") classId: Int,
        @Query("term") term: String? = null,
        @Query("year") year: String? = null
    ): Response<PrimaryTimetableResponse>

    @POST("admin/timetable/primary")
    suspend fun createPrimaryTimetable(
        @Body timetable: PrimaryTimetable
    ): Response<PrimaryTimetableResponse>

    @PUT("admin/timetable/primary/{id}")
    suspend fun updatePrimaryTimetable(
        @Path("id") id: Int,
        @Body timetable: PrimaryTimetable
    ): Response<PrimaryTimetableResponse>

    @DELETE("admin/timetable/primary/{id}")
    suspend fun deletePrimaryTimetable(@Path("id") id: Int): Response<Map<String, String>>

    @POST("admin/timetable/primary/periods")
    suspend fun addPeriods(
        @Body periods: List<TimetablePeriod>
    ): Response<PrimaryTimetableResponse>

    @PUT("admin/timetable/primary/periods/{periodId}")
    suspend fun updatePeriod(
        @Path("periodId") periodId: Int,
        @Body period: TimetablePeriod
    ): Response<TimetablePeriod>

    @DELETE("admin/timetable/primary/periods/{periodId}")
    suspend fun deletePeriod(@Path("periodId") periodId: Int): Response<Map<String, String>>

    @GET("admin/timetable/primary/conflicts")
    suspend fun getPrimaryConflicts(
        @Query("classId") classId: Int? = null
    ): Response<List<TimetableConflict>>

    @POST("admin/timetable/primary/conflicts/resolve/{conflictId}")
    suspend fun resolveConflict(@Path("conflictId") conflictId: Int): Response<Map<String, String>>

    // --- Pre-Primary Timetable ---
    @GET("admin/timetable/pre-primary/classes")
    suspend fun getPrePrimaryClasses(): Response<List<ClassInfo>>

    @GET("admin/timetable/pre-primary/{classId}")
    suspend fun getPrePrimaryTimetable(
        @Path("classId") classId: Int,
        @Query("term") term: String? = null,
        @Query("year") year: String? = null
    ): Response<PrePrimaryTimetableResponse>

    @POST("admin/timetable/pre-primary")
    suspend fun createPrePrimaryTimetable(
        @Body timetable: PrePrimaryTimetable
    ): Response<PrePrimaryTimetableResponse>

    @PUT("admin/timetable/pre-primary/{id}")
    suspend fun updatePrePrimaryTimetable(
        @Path("id") id: Int,
        @Body timetable: PrePrimaryTimetable
    ): Response<PrePrimaryTimetableResponse>

    @DELETE("admin/timetable/pre-primary/{id}")
    suspend fun deletePrePrimaryTimetable(@Path("id") id: Int): Response<Map<String, String>>

    @POST("admin/timetable/pre-primary/activities")
    suspend fun addPrePrimaryActivities(
        @Body activities: List<PrePrimaryActivity>
    ): Response<PrePrimaryTimetableResponse>

    @PUT("admin/timetable/pre-primary/activities/{activityId}")
    suspend fun updatePrePrimaryActivity(
        @Path("activityId") activityId: Int,
        @Body activity: PrePrimaryActivity
    ): Response<PrePrimaryActivity>

    @DELETE("admin/timetable/pre-primary/activities/{activityId}")
    suspend fun deletePrePrimaryActivity(@Path("activityId") activityId: Int): Response<Map<String, String>>

    // --- General Timetable ---
    @GET("admin/timetable/summary")
    suspend fun getTimetableSummary(): Response<TimetableSummary>

    @GET("admin/timetable/subjects")
    suspend fun getAllSubjects(): Response<List<Subject>>

    @GET("admin/timetable/teachers")
    suspend fun getAllTeachers(): Response<List<TeacherAssignment>>

    @GET("admin/timetable/rooms")
    suspend fun getAllRooms(): Response<List<String>>
}

// ==================== RESPONSE MODELS ====================

data class RefreshTokenResponse(
    val token: String,
    val message: String
)

data class AuthResponse(
    val token: String,
    val user: UserAccount
)

data class ParentStats(
    val childAttendance: String,
    val feeBalance: String
)

data class BursarStats(
    val todayCollection: String,
    val pendingFees: String
)

data class TeacherStats(
    val classesToday: String,
    val totalStudents: String,
    val avgScore: String
)

data class StudentStats(
    val rank: String,
    val avgScore: String
)

data class BooleanResponse(
    val exists: Boolean
)

data class AdminStats(
    val total_students: String,
    val total_staff: String,
    val attendance_rate: String,
    val fees_collected: String
)