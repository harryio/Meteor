package io.github.sainiharry.meteor.common.model

import io.github.sainiharry.meteor.common.UniqueId

data class News(
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