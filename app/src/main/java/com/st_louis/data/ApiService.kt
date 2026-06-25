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

    // ==================== TEACHER DASHBOARD & CLASS ENDPOINTS ====================

    // --- Teacher Dashboard ---
    @GET("teacher/dashboard/{teacherId}")
    suspend fun getTeacherDashboard(
        @Path("teacherId") teacherId: String
    ): Response<TeacherDashboardData>

    // --- Teacher Classes ---
    @GET("teacher/{teacherId}/classes")
    suspend fun getTeacherClasses(
        @Path("teacherId") teacherId: String
    ): Response<List<TeacherClass>>

    @GET("teacher/{teacherId}/classes/{section}")
    suspend fun getTeacherClassesBySection(
        @Path("teacherId") teacherId: String,
        @Path("section") section: String
    ): Response<List<TeacherClass>>

    @GET("teacher/class/{classId}")
    suspend fun getClassDetail(
        @Path("classId") classId: String
    ): Response<ClassDetail>

    @GET("teacher/{teacherId}/class/{classId}/students")
    suspend fun getClassStudents(
        @Path("teacherId") teacherId: String,
        @Path("classId") classId: String
    ): Response<List<Student>>

    @GET("teacher/class/{classId}/schedule")
    suspend fun getClassSchedule(
        @Path("classId") classId: String,
        @Query("day") day: String? = null
    ): Response<List<ClassSchedule>>

    @GET("teacher/{teacherId}/classes/summary")
    suspend fun getClassSummary(
        @Path("teacherId") teacherId: String,
        @Query("term") term: String? = null,
        @Query("year") year: String? = null
    ): Response<ClassSummary>

    // --- Teacher Students ---
    @GET("teacher/{teacherId}/students")
    suspend fun getTeacherStudents(
        @Path("teacherId") teacherId: String
    ): Response<List<Student>>

    @GET("teacher/{teacherId}/students/{classId}")
    suspend fun getTeacherStudentsByClass(
        @Path("teacherId") teacherId: String,
        @Path("classId") classId: String
    ): Response<List<Student>>

    // --- Teacher Subjects ---
    @GET("teacher/{teacherId}/subjects")
    suspend fun getTeacherSubjects(
        @Path("teacherId") teacherId: String
    ): Response<List<Subject>>

    @GET("teacher/class/{classId}/subjects")
    suspend fun getClassSubjects(
        @Path("classId") classId: String
    ): Response<List<Subject>>

    // --- Teacher Profile ---
    @GET("teacher/{teacherId}/profile")
    suspend fun getTeacherProfile(
        @Path("teacherId") teacherId: String
    ): Response<TeacherProfile>

    @PUT("teacher/{teacherId}/profile")
    suspend fun updateTeacherProfile(
        @Path("teacherId") teacherId: String,
        @Body profile: TeacherProfile
    ): Response<TeacherProfile>

    // --- Notifications ---
    @GET("teacher/{teacherId}/notifications")
    suspend fun getTeacherNotifications(
        @Path("teacherId") teacherId: String
    ): Response<List<Notification>>

    @PUT("teacher/notifications/{notificationId}/read")
    suspend fun markNotificationRead(
        @Path("notificationId") notificationId: String
    ): Response<SimpleApiResponse>

    @PUT("teacher/notifications/read-all")
    suspend fun markAllNotificationsRead(
        @Body request: MarkAllReadRequest
    ): Response<SimpleApiResponse>

    // ==================== ATTENDANCE ENDPOINTS ====================

    @GET("teacher/{teacherId}/attendance/today")
    suspend fun getTodayAttendance(
        @Path("teacherId") teacherId: String
    ): Response<AttendanceSummary>

    @GET("teacher/{teacherId}/attendance/{date}")
    suspend fun getAttendanceByDate(
        @Path("teacherId") teacherId: String,
        @Path("date") date: String
    ): Response<AttendanceSummary>

    @GET("teacher/{teacherId}/attendance/class/{classId}")
    suspend fun getClassAttendance(
        @Path("teacherId") teacherId: String,
        @Path("classId") classId: String,
        @Query("date") date: String? = null
    ): Response<AttendanceSummary>

    @GET("teacher/{teacherId}/attendance/student/{studentId}")
    suspend fun getStudentAttendance(
        @Path("teacherId") teacherId: String,
        @Path("studentId") studentId: String,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<StudentAttendance>

    @GET("teacher/{teacherId}/attendance/stats")
    suspend fun getAttendanceStats(
        @Path("teacherId") teacherId: String,
        @Query("class_id") classId: String? = null,
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null
    ): Response<AttendanceStats>

    @POST("teacher/attendance/mark")
    suspend fun markAttendance(
        @Body attendanceData: AttendanceRequest
    ): Response<ApiResponse<AttendanceData>>

    @PUT("teacher/attendance/{attendanceId}")
    suspend fun updateAttendance(
        @Path("attendanceId") attendanceId: String,
        @Body attendanceData: AttendanceRequest
    ): Response<ApiResponse<AttendanceData>>

    @PUT("teacher/attendance/student/{studentId}")
    suspend fun updateStudentAttendance(
        @Path("studentId") studentId: String,
        @Body record: AttendanceRecord
    ): Response<ApiResponse<AttendanceData>>

    @DELETE("teacher/attendance/{attendanceId}")
    suspend fun deleteAttendance(
        @Path("attendanceId") attendanceId: String
    ): Response<SimpleApiResponse>

    @GET("teacher/{teacherId}/attendance/export")
    suspend fun exportAttendance(
        @Path("teacherId") teacherId: String,
        @Query("class_id") classId: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("format") format: String = "pdf"
    ): Response<AttendanceExport>

    // ==================== RESULTS ENDPOINTS ====================

    @GET("teacher/{teacherId}/exams")
    suspend fun getExams(
        @Path("teacherId") teacherId: String
    ): Response<List<Exam>>

    @POST("teacher/results")
    suspend fun submitResults(
        @Body resultsData: ResultsRequest
    ): Response<ApiResponse<Any>>

    @GET("teacher/{teacherId}/results/{classId}/{examId}")
    suspend fun getClassResults(
        @Path("teacherId") teacherId: String,
        @Path("classId") classId: String,
        @Path("examId") examId: String
    ): Response<List<StudentResult>>

    @PUT("teacher/results/{resultId}")
    suspend fun updateResult(
        @Path("resultId") resultId: String,
        @Body resultData: ResultUpdateRequest
    ): Response<ApiResponse<Any>>

    @DELETE("teacher/results/{resultId}")
    suspend fun deleteResult(
        @Path("resultId") resultId: String
    ): Response<SimpleApiResponse>
}