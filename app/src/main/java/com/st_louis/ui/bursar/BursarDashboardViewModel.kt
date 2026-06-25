package com.st_louis.ui.bursar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BursarDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _bursarName = MutableLiveData<String>().apply { value = "Ms. Sarah Wanjiku" }
    val bursarName: LiveData<String> = _bursarName

    private val _todayCollection = MutableLiveData<String>().apply { value = "UGX 0" }
    val todayCollection: LiveData<String> = _todayCollection

    private val _pendingFees = MutableLiveData<String>().apply { value = "UGX 0" }
    val pendingFees: LiveData<String> = _pendingFees

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val response = apiService.getBursarStats()
                if (response.isSuccessful) {
                    val stats = response.body()
                    _todayCollection.value = stats?.todayCollection ?: "UGX 0"
                    _pendingFees.value = stats?.pendingFees ?: "UGX 0"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
