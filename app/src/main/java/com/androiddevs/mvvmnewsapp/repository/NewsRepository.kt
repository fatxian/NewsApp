package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article

/**
 * Repository for fetching news from the network and storing articles in the local database.
 * @param db The Room database instance.
 */
class NewsRepository(
    val db: ArticleDatabase
) {

    //Fetches breaking news from the API.
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    //Searches for news from the API.
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    //Inserts or updates an article in the database.
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    //Gets all saved articles from the database.
    fun getSavedNews() = db.getArticleDao().getAllArticles()

    //Deletes an article from the database.
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

}