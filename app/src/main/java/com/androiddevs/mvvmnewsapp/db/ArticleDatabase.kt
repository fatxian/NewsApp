package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

/**
 * The main database class for the application.
 * It's an abstract class that extends RoomDatabase.
 */
@Database(
    entities = [Article::class], // Defines the tables in the database.
    version = 1                  // Database version, used for migrations.
)
@TypeConverters(Converters::class) // Specifies type converters for custom objects.
abstract class ArticleDatabase : RoomDatabase() {

    /** Abstract function to get the Data Access Object for Articles. Room will implement this. */
    abstract fun getArticleDao(): ArticleDao

    /**
     * Companion object to provide a singleton instance of the database.
     * This ensures that only one database instance is active at a time.
     */
    companion object {
        // @Volatile makes writes to this field immediately visible to other threads.
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any() // A lock object to ensure thread safety.

        /**
         * The invoke operator allows creating a singleton instance like a function call.
         * It returns the existing instance or creates a new one in a thread-safe way.
         */
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        /** Private function to build the Room database. */
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db" // Name of the database file.
            ).build()
    }

}