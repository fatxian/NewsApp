package com.androiddevs.mvvmnewsapp.util
import com.androiddevs.mvvmnewsapp.BuildConfig

class Constants {

    /**
     * A companion object to hold constant values used throughout the application.
     */
    companion object {
        //It's recommended to move the API Key to a more secure place like local.properties.
        val API_KEY = BuildConfig.NEWS_API_KEY
        // The base URL for all News API requests.
        const val BASE_URL = "https://newsapi.org"
        // The delay in milliseconds to wait after user stops typing before making a search request.
        const val SEARCH_NEWS_TIME_DELAY = 500L
        // The number of articles to fetch per page for pagination.
        const val QUERY_PAGE_SIZE = 20
    }
}