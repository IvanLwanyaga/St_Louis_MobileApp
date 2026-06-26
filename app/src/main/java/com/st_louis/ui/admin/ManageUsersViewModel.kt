package com.st_louis.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.st_louis.data.repository.StudentRepository
import com.st_louis.models.Student
//import com.stlouis.data.repository.StudentRepository
//import com.stlouis.models.Student
import com.st_louis.utils.PasswordGenerator
import com.st_louis.utils.Result
import com.st_louis.utils.UserNameGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageUsersViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    application: Application
) : AndroidViewModel(application) {

    // UI State
    private val _uiState = MutableLiveData<ManageUsersUiState>()
    val uiState: LiveData<ManageUsersUiState> = _uiState

    // Registration Result
    private val _registrationResult = MutableLiveData<Result<Student>>()
    val registrationResult: LiveData<Result<Student>> = _registrationResult

    // Loading State
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Students List
    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    // Validation Errors
    private val _validationErrors = MutableLiveData<Map<String, String>>()
    val validationErrors: LiveData<Map<String, String>> = _validationErrors

    init {
        _uiState.value = ManageUsersUiState()
    }

    fun registerStudent(student: Student) {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = _uiState.value?.copy(isSubmitting = true)

            // Validate before submitting
            val errors = validateStudent(student)
            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _isLoading.value = false
                _uiState.value = _uiState.value?.copy(isSubmitting = false)
                return@launch
            }

            val result = studentRepository.registerStudent(student)
            _registrationResult.value = result

            if (result is Result.Success) {
                // Add student to the list
                val currentList = _students.value ?: emptyList()
                _students.value = currentList + result.data
                _uiState.value = _uiState.value?.copy(
                    isSubmitting = false,
                    registrationSuccess = true,
                    registeredStudent = result.data
                )
            } else {
                _uiState.value = _uiState.value?.copy(
                    isSubmitting = false,
                    registrationSuccess = false,
                    errorMessage = (result as Result.Error).exception.message
                )
            }

            _isLoading.value = false
        }
    }

    fun generateCredentials(name: String, className: String, admissionNumber: String, level: String): Pair<String, String> {
        val username = UserNameGenerator.generateForSchool(name, className, admissionNumber, level)
        val password = if (level == "PRE_PRIMARY") {
            PasswordGenerator.generatePrePrimaryPassword(name)
        } else {
            PasswordGenerator.generatePrimaryPassword(name)
        }
        return Pair(username, password)
    }

    fun validateStudent(student: Student): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (student.name.isBlank()) {
            errors["name"] = "Student's name is required"
        } else if (student.name.length < 3) {
            errors["name"] = "Name must be at least 3 characters"
        }

        if (student.admissionNumber.isBlank()) {
            errors["admissionNumber"] = "Admission number is required"
        } else if (student.admissionNumber.length < 4) {
            errors["admissionNumber"] = "Invalid admission number format"
        }

        if (student.parentEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(student.parentEmail).matches()) {
            errors["parentEmail"] = "Valid parent email is required"
        }

        if (student.parentPhone.isBlank()) {
            errors["parentPhone"] = "Parent phone number is required"
        } else if (student.parentPhone.replace(Regex("[^0-9+]"), "").length < 10) {
            errors["parentPhone"] = "Invalid phone number"
        }

        if (student.className.isBlank()) {
            errors["className"] = "Class is required"
        }

        if (student.stream.isBlank()) {
            errors["stream"] = "Stream/Section is required"
        }

        return errors
    }

    fun resetForm() {
        _uiState.value = ManageUsersUiState()
        _validationErrors.value = emptyMap()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = studentRepository.getAllStudents()
            if (result is Result.Success) {
                _students.value = result.data
            }
            _isLoading.value = false
        }
    }

    fun loadStudentsByLevel(level: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = studentRepository.getStudentsByLevel(level)
            if (result is Result.Success) {
                _students.value = result.data
            }
            _isLoading.value = false
        }
    }

    fun loadStudentsByClass(className: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = studentRepository.getStudentsByClass(className)
            if (result is Result.Success) {
                _students.value = result.data
            }
            _isLoading.value = false
        }
    }

    fun checkAdmissionNumberExists(admissionNumber: String): Boolean {
        var exists = false
        viewModelScope.launch {
            exists = studentRepository.isAdmissionNumberExists(admissionNumber)
        }
        return exists
    }

    fun checkUsernameExists(username: String): Boolean {
        var exists = false
        viewModelScope.launch {
            exists = studentRepository.isUsernameExists(username)
        }
        return exists
    }
}

data class ManageUsersUiState(
    val isSubmitting: Boolean = false,
    val registrationSuccess: Boolean = false,
    val registeredStudent: Student? = null,
    val errorMessage: String? = null,
    val selectedLevel: String = "PRE_PRIMARY",
    val selectedClass: String = "",
    val selectedStream: String = "",
    val generatedUsername: String = "",
    val generatedPassword: String = ""
)