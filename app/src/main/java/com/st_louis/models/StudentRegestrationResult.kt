package com.st_louis.models

/**
 * Represents the result of a student registration operation
 */
data class StudentRegistrationResult(
    val success: Boolean,
    val message: String,
    val student: Student? = null,
    val error: Exception? = null
) {
    companion object {
        /**
         * Create a success result
         */
        fun success(student: Student, message: String = "Student registered successfully"): StudentRegistrationResult {
            return StudentRegistrationResult(
                success = true,
                message = message,
                student = student,
                error = null
            )
        }

        /**
         * Create an error result
         */
        fun error(message: String, exception: Exception? = null): StudentRegistrationResult {
            return StudentRegistrationResult(
                success = false,
                message = message,
                student = null,
                error = exception
            )
        }
    }

    /**
     * Check if the operation was successful
     */
    fun isSuccess(): Boolean = success

    /**
     * Check if the operation failed
     */
    fun isError(): Boolean = !success

    /**
     * Get the student if success, or null if error
     */
    fun getStudentOrNull(): Student? = student

    /**
     * Get the error if error, or null if success
     */
    fun getErrorOrNull(): Exception? = error
}