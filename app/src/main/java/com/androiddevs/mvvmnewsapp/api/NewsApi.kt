package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A Retrofit interface to define the communication with the News API.                                                                     │
 * It declares the available endpoints and their request/response structures.
 */
interface NewsApi {

    /** Fetches top-headline news. */
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse> // The response is wrapped in a Retrofit Response object.

    /** Searches all news. */
    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") // "q" stands for query, the search keyword.
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

}