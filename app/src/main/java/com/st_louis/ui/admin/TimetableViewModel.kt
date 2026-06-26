package com.st_louis.ui.admin.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.*
import kotlinx.coroutines.launch

class TimetableViewModel(
    private val apiService: ApiService
) : ViewModel() {

    // Loading and Error
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Classes
    private val _primaryClasses = MutableLiveData<List<ClassInfo>>(emptyList())
    val primaryClasses: LiveData<List<ClassInfo>> = _primaryClasses

    private val _prePrimaryClasses = MutableLiveData<List<ClassInfo>>(emptyList())
    val prePrimaryClasses: LiveData<List<ClassInfo>> = _prePrimaryClasses

    // Timetables
    private val _primaryTimetable = MutableLiveData<PrimaryTimetableData?>()
    val primaryTimetable: LiveData<PrimaryTimetableData?> = _primaryTimetable

    private val _prePrimaryTimetable = MutableLiveData<PrePrimaryTimetableData?>()
    val prePrimaryTimetable: LiveData<PrePrimaryTimetableData?> = _prePrimaryTimetable

    // Summary
    private val _timetableSummary = MutableLiveData<TimetableSummary?>()
    val timetableSummary: LiveData<TimetableSummary?> = _timetableSummary

    // Operation Result
    private val _operationResult = MutableLiveData<Boolean?>()
    val operationResult: LiveData<Boolean?> = _operationResult

    fun loadClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load primary classes
                val primaryResponse = apiService.getPrimaryClasses()
                if (primaryResponse.isSuccessful) {
                    primaryResponse.body()?.let { _primaryClasses.value = it }
                }

                // Load pre-primary classes
                val prePrimaryResponse = apiService.getPrePrimaryClasses()
                if (prePrimaryResponse.isSuccessful) {
                    prePrimaryResponse.body()?.let { _prePrimaryClasses.value = it }
                }
            } catch (e: Exception) {
                _error.value = "Error loading classes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPrimaryTimetable(classId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPrimaryTimetable(classId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { _primaryTimetable.value = it }
                } else {
                    _error.value = "Failed to load primary timetable"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPrePrimaryTimetable(classId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPrePrimaryTimetable(classId)
                if (response.isSuccessful) {
                    response.body()?.data?.let { _prePrimaryTimetable.value = it }
                } else {
                    _error.value = "Failed to load pre-primary timetable"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTimetableSummary() {
        viewModelScope.launch {
            try {
                val response = apiService.getTimetableSummary()
                if (response.isSuccessful) {
                    response.body()?.let { _timetableSummary.value = it }
                }
            } catch (e: Exception) {
                // Silent fail for summary
            }
        }
    }

    fun addPrimaryPeriod(period: TimetablePeriod) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.addPeriods(listOf(period))
                if (response.isSuccessful) {
                    _operationResult.value = true
                    // Reload timetable
                    selectedClassId?.let { loadPrimaryTimetable(it) }
                } else {
                    _error.value = "Failed to add period"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePrimaryPeriod(periodId: Int, period: TimetablePeriod) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updatePeriod(periodId, period)
                if (response.isSuccessful) {
                    _operationResult.value = true
                    selectedClassId?.let { loadPrimaryTimetable(it) }
                } else {
                    _error.value = "Failed to update period"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePrimaryPeriod(periodId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deletePeriod(periodId)
                if (response.isSuccessful) {
                    _operationResult.value = true
                    selectedClassId?.let { loadPrimaryTimetable(it) }
                } else {
                    _error.value = "Failed to delete period"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resolveConflict(conflictId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.resolveConflict(conflictId)
                if (response.isSuccessful) {
                    _operationResult.value = true
                    selectedClassId?.let { loadPrimaryTimetable(it) }
                } else {
                    _error.value = "Failed to resolve conflict"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private var selectedClassId: Int? = null

    fun setSelectedClassId(classId: Int) {
        selectedClassId = classId
    }
}