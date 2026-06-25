package com.st_louis.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _studentName = MutableLiveData<String>().apply { value = "Peter Kamau" }
    val studentName: LiveData<String> = _studentName

    private val _className = MutableLiveData<String>().apply { value = "Grade 4A · Admission: SL/2026/042" }
    val className: LiveData<String> = _className

    private val _rank = MutableLiveData<String>().apply { value = "3rd" }
    val rank: LiveData<String> = _rank

    private val _avgScore = MutableLiveData<String>().apply { value = "88%" }
    val avgScore: LiveData<String> = _avgScore

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                // In a real app, you'd get the current user ID from session/pref
                val studentId = "current_student_id"
                val response = apiService.getStudentStats(studentId)
                if (response.isSuccessful) {
                    val stats = response.body()
                    _rank.value = stats?.rank ?: "3rd"
                    _avgScore.value = stats?.avgScore ?: "88%"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
