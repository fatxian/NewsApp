package com.androiddevs.mvvmnewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    val author: String? = null,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,  //Room 沒辦法處理不是原始的type，所以要用type converter class
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable  //because Article class is not primitive type, we should mark Serializable to tell
//kotlin that we want to be able to pass this class between several fragments with the navigation components
//todo:Parcelize