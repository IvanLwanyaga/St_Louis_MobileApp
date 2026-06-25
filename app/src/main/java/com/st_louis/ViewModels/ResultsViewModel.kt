package com.st_louis.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiClient
import com.st_louis.models.Exam
import com.st_louis.models.ApiResponse
import com.st_louis.models.Student
import com.st_louis.data.repository.ResultsRepository
import kotlinx.coroutines.launch

class ResultsViewModel : ViewModel() {

    private val repository = ResultsRepository(ApiClient.apiService)

    private val _sections = MutableLiveData<List<String>>()
    val sections: LiveData<List<String>> = _sections

    private val _exams = MutableLiveData<List<Exam>>()
    val exams: LiveData<List<Exam>> = _exams

    private val _classes = MutableLiveData<List<String>>()
    val classes: LiveData<List<String>> = _classes

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _resultsResult = MutableLiveData<ApiResponse<Any>?>()
    val resultsResult: LiveData<ApiResponse<Any>?> = _resultsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Track results for each student
    private val resultsMap = mutableMapOf<String, Pair<Int, String>>()

    fun loadSections() {
        // Load sections from API or local
        val sectionsList = listOf("Pre-Primary", "Primary")
        _sections.value = sectionsList
    }

    fun loadExams(teacherId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getExams(teacherId)
                if (response.isSuccessful) {
                    response.body()?.let { examList ->
                        _exams.value = examList
                    }
                } else {
                    _error.value = "Failed to load exams: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadClasses(teacherId: String, section: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getTeacherClasses(teacherId, section)
                if (response.isSuccessful) {
                    response.body()?.let { classList ->
                        _classes.value = classList.map { it.name }
                    }
                } else {
                    _error.value = "Failed to load classes: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadStudents(teacherId: String, className: String, examType: String, subject: String) {
        _isLoading.value = true
        resultsMap.clear()

        viewModelScope.launch {
            try {
                val response = repository.getStudentsByClass(teacherId, className)
                if (response.isSuccessful) {
                    response.body()?.let { studentList ->
                        _students.value = studentList
                    }
                } else {
                    _error.value = "Failed to load students: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStudentResult(studentId: String, score: Int, grade: String) {
        resultsMap[studentId] = Pair(score, grade)
    }

    fun getEnteredCount(): Int {
        return resultsMap.size
    }

    fun submitResults(teacherId: String, className: String, examType: String, subject: String, section: String) {
        _isLoading.value = true

        val resultsData = mapOf(
            "teacher_id" to teacherId,
            "class_name" to className,
            "exam_type" to examType,
            "subject" to subject,
            "section" to section,
            "results" to resultsMap.map { entry ->
                mapOf(
                    "student_id" to entry.key,
                    "score" to entry.value.first,
                    "grade" to entry.value.second
                )
            }
        )

        viewModelScope.launch {
            try {
                val response = repository.submitResults(resultsData)
                if (response.isSuccessful) {
                    _resultsResult.value = response.body()
                } else {
                    _error.value = "Failed to submit: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}