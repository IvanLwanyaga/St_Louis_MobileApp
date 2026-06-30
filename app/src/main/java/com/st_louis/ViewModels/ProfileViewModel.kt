package com.st_louis.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.st_louis.data.ApiClient
import com.st_louis.models.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> = _userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadUserProfile(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getUser(userId)
                if (response.isSuccessful) {
                    // map UserAccount to User if needed, or assume they are compatible for now
                    // response.body() is UserAccount. Let's check ApiService
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}