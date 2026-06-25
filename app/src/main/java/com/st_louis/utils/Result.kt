package com.st_louis.utils

/**
 * A sealed class representing the result of an operation.
 * Can be either Success with data or Error with an exception.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Represents a loading state
     */
    object Loading : Result<Nothing>()  // Add this if missing

    /**
     * Helper function to check if result is successful
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Helper function to check if result is an error
     */
    fun isError(): Boolean = this is Error

    /**
     * Helper function to check if result is loading
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Get the data if success, or null if error
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
        is Loading -> null
    }

    /**
     * Get the exception if error, or null if success
     */
    fun exceptionOrNull(): Exception? = when (this) {
        is Success -> null
        is Error -> exception
        is Loading -> null
    }

    /**
     * Fold function to handle both cases
     */
    fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Exception) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
        is Loading -> onLoading()
    }
}