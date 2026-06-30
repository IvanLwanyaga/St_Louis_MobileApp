package com.st_louis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.models.ClassSchedule
import com.st_louis.models.TeacherClass
import com.st_louis.data.Repository.ClassRepository
import kotlinx.coroutines.launch

class ClassesViewModel : ViewModel() {

    private val classRepository = ClassRepository()

    // ==================== LIVE DATA ====================

    private val _classes = MutableLiveData<List<TeacherClass>?>()
    val classes: LiveData<List<TeacherClass>> = _classes as LiveData<List<TeacherClass>>

    private val _classSchedule = MutableLiveData<List<ClassSchedule>>()
    val classSchedule: LiveData<List<ClassSchedule>> = _classSchedule

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String> = _error as LiveData<String>

    private val _totalClasses = MutableLiveData<Int>()
    val totalClasses: LiveData<Int> = _totalClasses

    private val _todayClasses = MutableLiveData<List<TeacherClass>>()
    val todayClasses: LiveData<List<TeacherClass>> = _todayClasses

    // ==================== LOAD CLASSES ====================

    fun loadClasses(teacherId: String) {
        if (teacherId.isEmpty()) {
            _error.value = "Teacher ID is required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = classRepository.getTeacherClasses(teacherId)
                if (response.isSuccessful) {
                    response.body()?.let { classList ->
                        _classes.value = classList
                        _totalClasses.value = classList.size
                        filterTodayClasses(classList)
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

    fun loadClassesBySection(teacherId: String, section: String) {
        if (teacherId.isEmpty() || section.isEmpty()) {
            _error.value = "Teacher ID and Section are required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = classRepository.getTeacherClassesBySection(teacherId, section)
                if (response.isSuccessful) {
                    response.body()?.let { classList ->
                        _classes.value = classList
                        _totalClasses.value = classList.size
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

    // ==================== CLASS SCHEDULE ====================

    fun loadClassSchedule(classId: String, day: String? = null) {
        if (classId.isEmpty()) {
            _error.value = "Class ID is required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = classRepository.getClassSchedule(classId, day)
                if (response.isSuccessful) {
                    response.body()?.let { schedule ->
                        _classSchedule.value = schedule
                    }
                } else {
                    _error.value = "Failed to load schedule: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading schedule: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== CLASS SUMMARY ====================

    fun loadClassSummary(teacherId: String, term: String? = null, year: String? = null) {
        if (teacherId.isEmpty()) {
            _error.value = "Teacher ID is required"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = classRepository.getClassSummary(teacherId, term, year)
                if (response.isSuccessful) {
                    response.body()?.let { summary ->
                        _classes.value = summary.classes
                        _totalClasses.value = summary.totalClasses
                    }
                } else {
                    _error.value = "Failed to load summary: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading summary: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== HELPERS ====================

    private fun filterTodayClasses(classes: List<TeacherClass>) {
        val today = getTodayDayOfWeek()
        val todayClassList = classes.filter { it.day?.equals(today, ignoreCase = true) == true }
        _todayClasses.value = todayClassList
    }

    private fun getTodayDayOfWeek(): String {
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            java.util.Calendar.MONDAY -> "Monday"
            java.util.Calendar.TUESDAY -> "Tuesday"
            java.util.Calendar.WEDNESDAY -> "Wednesday"
            java.util.Calendar.THURSDAY -> "Thursday"
            java.util.Calendar.FRIDAY -> "Friday"
            java.util.Calendar.SATURDAY -> "Saturday"
            java.util.Calendar.SUNDAY -> "Sunday"
            else -> "Monday"
        }
    }

    fun getClassCount(): Int {
        return _classes.value?.size ?: 0
    }

    fun getClassNames(): List<String> {
        return _classes.value?.map { it.name } ?: emptyList()
    }

    fun getClassById(classId: String): TeacherClass? {
        return _classes.value?.find { it.id == classId }
    }

    fun getClassesBySubject(subject: String): List<TeacherClass> {
        return _classes.value?.filter { it.subject == subject } ?: emptyList()
    }

    fun getPrimaryClasses(): List<TeacherClass> {
        return _classes.value?.filter { it.section == "Primary" } ?: emptyList()
    }

    fun getPrePrimaryClasses(): List<TeacherClass> {
        return _classes.value?.filter { it.section == "Pre-Primary" } ?: emptyList()
    }

    fun getTodayClasses(): List<TeacherClass> {
        return _todayClasses.value ?: emptyList()
    }

    fun getClassesWithMostStudents(): TeacherClass? {
        return _classes.value?.maxByOrNull { it.studentsCount }
    }

    fun getClassesWithLeastStudents(): TeacherClass? {
        return _classes.value?.minByOrNull { it.studentsCount }
    }

    fun getTotalStudents(): Int {
        return _classes.value?.sumOf { it.studentsCount } ?: 0
    }

    // ==================== RESET ====================

    fun reset() {
        _classes.value = emptyList()
        _classSchedule.value = emptyList()
        _todayClasses.value = emptyList()
        _totalClasses.value = 0
        _error.value = null
    }
}