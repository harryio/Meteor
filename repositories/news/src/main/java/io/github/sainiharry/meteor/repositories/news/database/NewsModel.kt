package io.github.sainiharry.meteor.repositories.news.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class NewsModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)