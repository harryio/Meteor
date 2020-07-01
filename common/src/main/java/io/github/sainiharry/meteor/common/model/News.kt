package io.github.sainiharry.meteor.common.model

import io.github.sainiharry.meteor.common.UniqueId

data class News(
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
) : UniqueId {

    var id: Long = 0

    override fun getUniqueId(): Long = title.hashCode().toLong()
}