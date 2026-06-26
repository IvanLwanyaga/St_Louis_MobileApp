package com.st_louis.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.ScheduleData
import com.st_louis.models.ScheduleEvent
import com.st_louis.models.ScheduleStats
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class ScheduleViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _scheduleData = MutableLiveData<ScheduleData?>()
    val scheduleData: LiveData<ScheduleData?> = _scheduleData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _events = MutableLiveData<List<ScheduleEvent>>(emptyList())
    val events: LiveData<List<ScheduleEvent>> = _events

    private val _stats = MutableLiveData<ScheduleStats?>()
    val stats: LiveData<ScheduleStats?> = _stats

    private val _operationResult = MutableLiveData<Boolean?>()
    val operationResult: LiveData<Boolean?> = _operationResult

    private var allEvents: List<ScheduleEvent> = emptyList()

    fun loadSchedule(month: Int, year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.getSchedule(month, year)
                handleResponse(response)
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleResponse(response: Response<com.st_louis.models.ScheduleResponse>) {
        if (response.isSuccessful) {
            val scheduleResponse = response.body()
            if (scheduleResponse != null && scheduleResponse.status) {
                scheduleResponse.data?.let { data ->
                    _scheduleData.value = data
                    allEvents = data.events
                    _events.value = data.events
                    _stats.value = data.stats
                }
            } else {
                _error.value = scheduleResponse?.message ?: "Failed to load schedule"
            }
        } else {
            _error.value = "Server error: ${response.code()}"
        }
    }

    fun filterEvents(eventType: String) {
        if (allEvents.isEmpty()) return
        val filtered = if (eventType == "all") {
            allEvents
        } else {
            allEvents.filter { it.eventType.equals(eventType, ignoreCase = true) }
        }
        _events.value = filtered
    }

    fun createEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.createEvent(event)
                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    if (scheduleResponse != null && scheduleResponse.status) {
                        _operationResult.value = true
                        // Reload with current month/year
                        val calendar = Calendar.getInstance()
                        loadSchedule(
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.YEAR)
                        )
                    } else {
                        _error.value = scheduleResponse?.message ?: "Failed to create event"
                        _operationResult.value = false
                    }
                } else {
                    _error.value = "Server error: ${response.code()}"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEvent(id: Int, event: ScheduleEvent) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.updateEvent(id, event)
                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    if (scheduleResponse != null && scheduleResponse.status) {
                        _operationResult.value = true
                        // Update local lists
                        val updatedEvents = allEvents.map {
                            if (it.id == id) event.copy(id = id) else it
                        }
                        allEvents = updatedEvents
                        _events.value = updatedEvents
                    } else {
                        _error.value = scheduleResponse?.message ?: "Failed to update event"
                        _operationResult.value = false
                    }
                } else {
                    _error.value = "Server error: ${response.code()}"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.deleteEvent(id)
                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    if (scheduleResponse != null && scheduleResponse.status) {
                        _operationResult.value = true
                        // Remove from local lists
                        allEvents = allEvents.filter { it.id != id }
                        _events.value = allEvents
                    } else {
                        _error.value = scheduleResponse?.message ?: "Failed to delete event"
                        _operationResult.value = false
                    }
                } else {
                    _error.value = "Server error: ${response.code()}"
                    _operationResult.value = false
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                _operationResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getEventsForDate(date: String): List<ScheduleEvent> {
        return _events.value?.filter { it.date == date } ?: emptyList()
    }
}