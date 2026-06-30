package com.st_louis.ui.finance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiService
import com.st_louis.models.FeeDetailsResponse
import com.st_louis.models.FeeSummary
import com.st_louis.models.Payment
import kotlinx.coroutines.launch

class FeeBalanceViewModel(private val apiService: ApiService) : ViewModel() {

    private val _feeSummary = MutableLiveData<FeeSummary>()
    val feeSummary: LiveData<FeeSummary> = _feeSummary

    private val _paymentHistory = MutableLiveData<List<Payment>>()
    val paymentHistory: LiveData<List<Payment>> = _paymentHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFeeDetails(studentId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = apiService.getFeeDetails(studentId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _feeSummary.value = it.summary
                        _paymentHistory.value = it.history
                    }
                } else {
                    _error.value = "Failed to load fee details: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "An error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
