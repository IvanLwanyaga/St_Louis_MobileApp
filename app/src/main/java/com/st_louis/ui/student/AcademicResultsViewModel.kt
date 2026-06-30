package com.st_louis.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.AcademicResultsResponse
import com.st_louis.models.ExamPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademicResultsViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val _examPeriods = MutableLiveData<List<ExamPeriod>>()
    val examPeriods: LiveData<List<ExamPeriod>> = _examPeriods

    private val _results = MutableLiveData<AcademicResultsResponse?>()
    val results: LiveData<AcademicResultsResponse?> = _results

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadExamPeriods(studentId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getExamPeriods(studentId)
                if (response.isSuccessful) {
                    _examPeriods.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load exam periods"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadResults(studentId: String, periodId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = apiService.getAcademicResults(studentId, periodId)
                if (response.isSuccessful) {
                    _results.value = response.body()
                } else if (response.code() == 404) {
                    _results.value = null 
                } else {
                    _error.value = "Failed to load results"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
