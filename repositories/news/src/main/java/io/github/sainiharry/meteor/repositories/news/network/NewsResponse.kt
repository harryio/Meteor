package io.github.sainiharry.meteor.repositories.news.network

import io.github.sainiharry.meteor.repositories.news.News

data class NewsResponse(
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)

private fun NewsResponse.toNews() =
    News(0, author, title, description, url, urlToImage, publishedAt, content)

internal fun List<NewsResponse>.toNews() = map { it.toNews() }