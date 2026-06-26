package com.st_louis.ui.parent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _parentName = MutableLiveData<String>().apply { value = "Mr. David Kamau" }
    val parentName: LiveData<String> = _parentName

    private val _childInfo = MutableLiveData<String>().apply { value = "Parent of: Peter Kamau (Grade 4A)" }
    val childInfo: LiveData<String> = _childInfo

    private val _childAttendance = MutableLiveData<String>().apply { value = "98%" }
    val childAttendance: LiveData<String> = _childAttendance

    private val _feeBalance = MutableLiveData<String>().apply { value = "UGX 50,000" }
    val feeBalance: LiveData<String> = _feeBalance

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val parentId = "current_parent_id"
                val response = apiService.getParentStats(parentId)
                if (response.isSuccessful) {
                    val stats = response.body()
                    _childAttendance.value = stats?.childAttendance ?: "98%"
                    _feeBalance.value = stats?.feeBalance ?: "UGX 50,000"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
