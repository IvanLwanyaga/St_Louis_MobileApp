package com.st_louis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

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
    val error: LiveData<String?> = _error

    // ==================== ATTENDANCE TRACKING ====================

    private val attendanceMap = mutableMapOf<String, Boolean>()
    private var currentStudents: List<Student> = emptyList()

    // ==================== SECTIONS ====================

    fun loadSections() {
        _sections.value = listOf("Pre-Primary", "Primary")
    }

    // ==================== CLASSES ====================

    fun loadClasses(teacherId: String, section: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // In a real API, you might filter by section
                val response = apiService.getTeacherClasses(teacherId)
                if (response.isSuccessful) {
                    _classes.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load classes"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== STUDENTS ====================

    fun loadStudents(teacherId: String, classId: String) {
        _isLoading.value = true
        attendanceMap.clear()
        viewModelScope.launch {
            try {
                val response = apiService.getClassStudents(teacherId, classId)
                if (response.isSuccessful) {
                    val studentList = response.body() ?: emptyList()
                    currentStudents = studentList
                    _students.value = studentList
                    studentList.forEach { attendanceMap[it.id] = true } 
                } else {
                    _error.value = "Failed to load students"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAttendance(studentId: String, isPresent: Boolean) {
        attendanceMap[studentId] = isPresent
    }

    fun getMarkedCount(): Int {
        return attendanceMap.size
    }

    fun submitAttendance(
        teacherId: String,
        classId: String,
        className: String,
        section: String,
        date: String
    ) {
        _isLoading.value = true
        val attendanceList = attendanceMap.map { (id, present) ->
            mapOf("student_id" to id, "present" to present)
        }

        val data = mapOf(
            "teacher_id" to teacherId,
            "class_id" to classId,
            "class_name" to className,
            "date" to date,
            "section" to section,
            "attendance" to attendanceList
        )

        viewModelScope.launch {
            try {
                val response = apiService.markAttendance(data)
                if (response.isSuccessful) {
                    _attendanceResult.value = ApiResponse(success = true, message = "Success", data = null)
                } else {
                    _error.value = "Failed to submit attendance"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        // Implement logout
    }
}
