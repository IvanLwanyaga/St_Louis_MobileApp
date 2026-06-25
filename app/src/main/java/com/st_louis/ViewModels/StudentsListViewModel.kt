package com.st_louis.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiClient
import com.st_louis.models.Student
import com.st_louis.models.TeacherClass
import kotlinx.coroutines.launch

class StudentsListViewModel : ViewModel() {
    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _classes = MutableLiveData<List<TeacherClass>>()
    val classes: LiveData<List<TeacherClass>> = _classes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadClasses(teacherId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTeacherClasses(teacherId)
                if (response.isSuccessful) _classes.value = response.body()
            } catch (e: Exception) {}
        }
    }

    fun loadStudents(teacherId: String, classId: String? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = if (classId != null) {
                    ApiClient.apiService.getClassStudents(teacherId, classId)
                } else {
                    ApiClient.apiService.getTeacherStudents(teacherId)
                }
                if (response.isSuccessful) {
                    _students.value = response.body()
                } else {
                    _error.value = response.message()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchStudents(query: String) {
        // Implement search logic locally or via API
    }
}