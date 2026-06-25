package com.st_louis.ui.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _teacherName = MutableLiveData<String>().apply { value = "Mr. John Otieno" }
    val teacherName: LiveData<String> = _teacherName

    private val _subject = MutableLiveData<String>().apply { value = "Mathematics & Physics" }
    val subject: LiveData<String> = _subject

    private val _classesToday = MutableLiveData<String>().apply { value = "4" }
    val classesToday: LiveData<String> = _classesToday

    private val _totalStudents = MutableLiveData<String>().apply { value = "155" }
    val totalStudents: LiveData<String> = _totalStudents

    private val _avgScore = MutableLiveData<String>().apply { value = "71%" }
    val avgScore: LiveData<String> = _avgScore

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                val teacherId = "current_teacher_id"
                val response = apiService.getTeacherStats(teacherId)
                if (response.isSuccessful) {
                    val stats = response.body()
                    _classesToday.value = stats?.classesToday ?: "4"
                    _totalStudents.value = stats?.totalStudents ?: "155"
                    _avgScore.value = stats?.avgScore ?: "71%"
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
