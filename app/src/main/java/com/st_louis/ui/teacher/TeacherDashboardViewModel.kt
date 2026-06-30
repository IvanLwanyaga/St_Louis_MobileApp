package com.st_louis.ui.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.TeacherStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _stats = MutableLiveData<TeacherStats>()
    val stats: LiveData<TeacherStats> = _stats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDashboardData(teacherId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getTeacherStats(teacherId)
                if (response.isSuccessful) {
                    _stats.value = response.body()
                } else {
                    _error.value = "Failed to fetch dashboard data"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        // Implement logout logic if needed
    }
}
