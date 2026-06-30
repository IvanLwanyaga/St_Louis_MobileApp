package com.st_louis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.AttendanceData
import com.st_louis.models.Student
import com.st_louis.models.TeacherClass
import com.st_louis.data.Repository.ClassRepository
import com.st_louis.data.repository.AttendanceRepository
import com.st_louis.models.ApiResponse
import kotlinx.coroutines.launch

class AttendanceViewModel : ViewModel() {

    private val attendanceRepository = AttendanceRepository(
        apiService = TODO()
    )
    private val classRepository = ClassRepository()

    // ==================== LIVE DATA ====================

    private val _sections = MutableLiveData<List<String>>()
    val sections: LiveData<List<String>> = _sections

    private val _classes = MutableLiveData<List<TeacherClass>>()
    val classes: LiveData<List<TeacherClass>> = _classes

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _attendanceResult = MutableLiveData<ApiResponse<AttendanceData>?>()
    val attendanceResult: LiveData<ApiResponse<AttendanceData>?> = _attendanceResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String> = _error as LiveData<String>

    // ==================== ATTENDANCE TRACKING ====================

    // Track attendance status for each student
    private val attendanceMap = mutableMapOf<String, Boolean>()
    private var currentStudents: List<Student> = emptyList()

    // ==================== INIT ====================

    init {
        loadSections()
    }

    // ==================== SECTIONS ====================

    fun loadSections() {
        val sectionList = listOf("Pre-Primary", "Primary")
        _sections.value = sectionList
    }

    // ==================== CLASSES ====================

    fun loadClasses(teacherId: String, section: String) {
        if (teacherId.isEmpty()) {
            _error.value = "Teacher ID is required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = classRepository.getTeacherClassesBySection(teacherId, section)
                if (response.isSuccessful) {
                    response.body()?.let { classList ->
                        _classes.value = classList
                    }
                } else {
                    _error.value = "Failed to load classes: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading classes: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== STUDENTS ====================

    fun loadStudents(teacherId: String, classId: String) {
        if (teacherId.isEmpty() || classId.isEmpty()) {
            _error.value = "Teacher ID and Class ID are required"
            return
        }

        _isLoading.value = true
        attendanceMap.clear()

        viewModelScope.launch {
            try {
                val response = attendanceRepository.getStudentsByClass(teacherId, classId)
                if (response.isSuccessful) {
                    response.body()?.let { studentList ->
                        currentStudents = studentList
                        _students.value = studentList
                        // Initialize all as not marked
                        studentList.forEach { attendanceMap[it.id] = false }
                    }
                } else {
                    _error.value = "Failed to load students: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading students: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== ATTENDANCE MANAGEMENT ====================

    fun updateAttendance(studentId: String, isPresent: Boolean) {
        attendanceMap[studentId] = isPresent
    }

    fun getMarkedCount(): Int {
        return attendanceMap.values.count { true }
    }

    fun getAttendanceMap(): Map<String, Boolean> {
        return attendanceMap.toMap()
    }

    fun getStudentAttendanceStatus(studentId: String): Boolean? {
        return attendanceMap[studentId]
    }

    fun clearAttendance() {
        attendanceMap.clear()
        currentStudents.forEach { attendanceMap[it.id] = false }
    }

    // ==================== SUBMIT ATTENDANCE ====================

    fun submitAttendance(
        teacherId: String,
        classId: String,
        className: String,
        section: String,
        date: String
    ) {
        if (teacherId.isEmpty() || classId.isEmpty()) {
            _error.value = "Teacher ID and Class ID are required"
            return
        }

        if (attendanceMap.isEmpty()) {
            _error.value = "No attendance data to submit"
            return
        }

        _isLoading.value = true

        // Build attendance data
        val attendanceList = attendanceMap.map { (studentId, isPresent) ->
            mapOf(
                "student_id" to studentId,
                "present" to isPresent
            )
        }

        val attendanceData = mapOf(
            "teacher_id" to teacherId,
            "class_id" to classId,
            "class_name" to className,
            "date" to date,
            "section" to section,
            "attendance" to attendanceList
        )

        viewModelScope.launch {
            try {
                val response = attendanceRepository.submitAttendance(attendanceData)
                if (response.isSuccessful) {
                    _attendanceResult.value = response.body()
                } else {
                    _error.value = "Failed to submit attendance: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error submitting attendance: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== GET ATTENDANCE ====================

    fun getTodayAttendance(teacherId: String) {
        if (teacherId.isEmpty()) {
            _error.value = "Teacher ID is required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = attendanceRepository.getTodayAttendance(teacherId)
                if (response.isSuccessful) {
                    // Handle today's attendance data
                    response.body()?.let { summary ->
                        // Update UI with attendance summary
                        _students.value = currentStudents
                    }
                } else {
                    _error.value = "Failed to get today's attendance: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error getting attendance: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== RESET ====================

    fun reset() {
        attendanceMap.clear()
        currentStudents = emptyList()
        _students.value = emptyList()
        _classes.value = emptyList()
        _attendanceResult.value = null
        _error.value = null
    }

    // ==================== HELPER METHODS ====================

    fun isStudentMarked(studentId: String): Boolean {
        return attendanceMap.containsKey(studentId)
    }

    fun getMarkedPercentage(): Float {
        val total = currentStudents.size
        if (total == 0) return 0f
        val marked = getMarkedCount()
        return (marked.toFloat() / total) * 100
    }

    fun getUnmarkedStudents(): List<Student> {
        return currentStudents.filter { !attendanceMap.containsKey(it.id) }
    }

    fun getPresentStudents(): List<Student> {
        return currentStudents.filter { attendanceMap[it.id] == true }
    }

    fun getAbsentStudents(): List<Student> {
        return currentStudents.filter { attendanceMap[it.id] == false }
    }
}