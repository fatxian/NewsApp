package com.androiddevs.mvvmnewsapp.models

/**
 * Data class that represents the top-level response from the News API.
 * It models the entire JSON object returned by the server.
 */
data class NewsResponse(
    // A list of news articles returned by the API.
    val articles: MutableList<Article>,
    // The status of the API request (e.g., "ok", "error").
    val status: String,
    // The total number of results available for the request.
    val totalResults: Int
)