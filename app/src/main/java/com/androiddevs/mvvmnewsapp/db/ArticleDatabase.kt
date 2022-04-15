package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile //other threads can immediately see when a thread change this instance
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        //this function will be called whenever we create an instance (initialize or instantiate)
        //of our database
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            //means this block can't be accessed by other threads at the same time
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }

}