package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

/**
 * Defines database operations for the Article entity using Room.
 */
@Dao
interface ArticleDao {

    /** Inserts or replaces an article in the database. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    /** Retrieves all articles from the database as LiveData. */
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    /** Deletes an article from the database. */
    @Delete
    suspend fun deleteArticle(article: Article)

}