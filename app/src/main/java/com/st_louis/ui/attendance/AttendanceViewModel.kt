package com.st_louis.ui.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.AttendanceRecord
import kotlinx.coroutines.launch

class AttendanceViewModel(private val apiService: ApiService) : ViewModel() {

    private val _attendancePercentage = MutableLiveData<String>().apply { value = "0" }
    val attendancePercentage: LiveData<String> = _attendancePercentage

    private val _attendanceRecords = MutableLiveData<List<AttendanceRecord>>()
    val attendanceRecords: LiveData<List<AttendanceRecord>> = _attendanceRecords

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAttendance(studentId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Using teacher/student specific endpoint
                val response = apiService.getStudentAttendance("T001", studentId)
                if (response.isSuccessful) {
                    val data = response.body()
                    // Map rate to string percentage
                    _attendancePercentage.value = String.format("%.0f", (data?.attendanceRate ?: 0.0) * 100)
                    _attendanceRecords.value = data?.records ?: emptyList()
                } else {
                    _error.value = "Failed to load attendance data"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAttendance(status: String) {
        // Implement marking logic if required
    }
}
