package com.androiddevs.mvvmnewsapp.util

//used to wrap around our network response
//differentiate between successful and error response
//handle the loading state so when we make a response that we can show on a progress bar
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}