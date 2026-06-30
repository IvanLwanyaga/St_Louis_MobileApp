package com.st_louis.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.AdminStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _stats = MutableLiveData<AdminStats>()
    val stats: LiveData<AdminStats> = _stats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchDashboardData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getAdminStats()
                if (response.isSuccessful) {
                    _stats.value = response.body()
                } else {
                    _error.value = "Failed to load admin stats"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
