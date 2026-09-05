package com.orbit.spatialjournal.core.util

/** Lightweight sealed result wrapper used across repositories and use cases. */
sealed class OrbitResult<out T> {
    data class Success<T>(val data: T) : OrbitResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : OrbitResult<Nothing>()
    data object Loading : OrbitResult<Nothing>()

    inline fun onSuccess(action: (T) -> Unit): OrbitResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String) -> Unit): OrbitResult<T> {
        if (this is Error) action(message)
        return this
    }
}
