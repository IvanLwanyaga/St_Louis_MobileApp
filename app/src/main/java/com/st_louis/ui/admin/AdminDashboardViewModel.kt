package com.st_louis.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _studentCount = MutableLiveData<String>().apply { value = "0" }
    val studentCount: LiveData<String> = _studentCount

    private val _staffCount = MutableLiveData<String>().apply { value = "0" }
    val staffCount: LiveData<String> = _staffCount

    private val _attendanceRate = MutableLiveData<String>().apply { value = "0%" }
    val attendanceRate: LiveData<String> = _attendanceRate

    private val _feesCollected = MutableLiveData<String>().apply { value = "0%" }
    val feesCollected: LiveData<String> = _feesCollected

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            try {
                val response = apiService.getAdminStats()
                if (response.isSuccessful) {
                    val stats = response.body()
                    _studentCount.value = stats?.total_students ?: "0"
                    _staffCount.value = stats?.total_staff ?: "0"
                    _attendanceRate.value = stats?.attendance_rate ?: "0%"
                    _feesCollected.value = stats?.fees_collected ?: "0%"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
