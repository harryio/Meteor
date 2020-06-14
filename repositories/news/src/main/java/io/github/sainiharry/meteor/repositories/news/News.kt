package io.github.sainiharry.meteor.repositories.news

import androidx.room.PrimaryKey
import io.github.sainiharry.meteor.common.UniqueId

data class News(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
) : UniqueId {
    override fun getUniqueId(): Long = id
}