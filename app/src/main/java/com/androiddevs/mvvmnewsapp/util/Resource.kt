package com.androiddevs.mvvmnewsapp.util

/**
 * A generic sealed class that represents the state of data being loaded from a source.
 * It is used to wrap network responses to differentiate between success, error, and loading states.
 * @param T The type of the data being held.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /** Represents a successful state with data. */
    class Success<T>(data: T) : Resource<T>(data)

    /** Represents an error state with a message and optional data. */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /** Represents the loading state. */
    class Loading<T> : Resource<T>()
}