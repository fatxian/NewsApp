package com.androiddevs.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.util.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

/**
 * ViewModel for news-related data.
 * It is responsible for preparing and managing the data for the UI.
 * It communicates with the NewsRepository to fetch and save data.
 */
class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    // LiveData for breaking news from the API
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    // Current page for breaking news pagination
    var breakingNewsPage = 1
    // Holds the response for breaking news to handle pagination
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    // Keeps track of the last search query to reset pagination on new search
    var lastSearchQuery: String? = null

    init {
        // Fetch breaking news for the US on initialization
        getBreakingNews("us")
    }

    /**
     * Fetches breaking news from the repository.
     * viewModelScope ensures the coroutine lives as long as the ViewModel.
     * @param countryCode The country code to fetch news for (e.g., "us").
     */
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    /**
     * Searches for news based on a query.
     * Resets pagination if the search query is new.
     * @param searchQuery The query to search for.
     */
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        // If the search query is new, reset pagination and previous results
        if (searchQuery != lastSearchQuery) {
            searchNewsPage = 1
            searchNewsResponse = null
            lastSearchQuery = searchQuery
        }
        safeSearchNewsCall(searchQuery)
    }

    /**
     * Handles the response from the breaking news API call.
     * Manages pagination by appending new articles to the existing list.
     * @param response The response from the Retrofit API call.
     * @return A Resource object representing the state (Success or Error).
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                // Increment page number for the next request
                breakingNewsPage++
                // If it's the first page, set the response directly
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    // Append new articles to the old list
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /**
     * Handles the response from the search news API call.
     * Manages pagination for search results.
     * @param response The response from the Retrofit API call.
     * @return A Resource object representing the state (Success or Error).
     */
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /**
     * Saves an article to the local database.
     * @param article The article to be saved.
     */
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    /**
     * Retrieves all saved news articles from the database.
     * @return LiveData list of articles.
     */
    fun getSavedNews() = newsRepository.getSavedNews()

    /**
     * Deletes an article from the local database.
     * @param article The article to be deleted.
     */
    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    /**
     * A safe wrapper for the breaking news API call.
     * Checks for internet connection and handles exceptions.
     * @param countryCode The country code for the news.
     */
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error")) // JSON parsing error
            }
        }
    }

    /**
     * A safe wrapper for the search news API call.
     * Checks for internet connection and handles exceptions.
     * @param searchQuery The query to search for.
     */
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error")) // JSON parsing error
            }
        }
    }

    /**
     * Checks if the device has an active internet connection.
     * @return True if connected, false otherwise.
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            // Used for older Android versions below Marshmallow (API 23)
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
