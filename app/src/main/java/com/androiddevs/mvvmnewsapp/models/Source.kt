package com.androiddevs.mvvmnewsapp.models

/**
 * Data class representing the source of a news article (e.g., "CNN", "BBC News").
 * This is a nested object within the Article model.
 */
data class Source(
    // The unique identifier for the news source (can be null).
    val id: Any? = null,
    // The display name of the news source.
    val name: String
)