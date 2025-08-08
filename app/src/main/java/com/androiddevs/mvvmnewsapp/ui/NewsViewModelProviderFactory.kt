package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository

/**
 * A factory for creating instances of NewsViewModel.
 * This is necessary because NewsViewModel has a constructor that takes dependencies (Application and NewsRepository),
 * and this factory tells the ViewModelProvider how to construct it.
 */
class NewsViewModelProviderFactory(
    // The Application context, required by AndroidViewModel
    val app: Application,
    // The repository that provides data to the ViewModel
    val newsRepository: NewsRepository
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given `Class`.
     * @param modelClass A `Class` whose instance is requested.
     * @return A newly created ViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is NewsViewModel
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            // Create and return an instance of NewsViewModel, passing the required dependencies
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(app, newsRepository) as T
        }
        // If the class is unknown, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}