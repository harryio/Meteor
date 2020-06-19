package io.github.sainiharry.meteor.repositories.news.network

import com.squareup.moshi.JsonClass
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.repositories.news.database.NewsModel

@JsonClass(generateAdapter = true)
internal data class NewsResponseWrapper(val articles: List<NewsResponse>)

@JsonClass(generateAdapter = true)
internal data class NewsResponse(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

internal fun List<NewsResponse>.toNewsList(): List<News> = map {
    News(
        0,
        it.author ?: "",
        it.title ?: "",
        it.description ?: "",
        it.url ?: "",
        it.urlToImage ?: "",
        it.publishedAt ?: "",
        it.content ?: ""
    )
}

internal fun List<NewsResponse>.toNewsModelList(): List<NewsModel> = map {
    NewsModel(
        0,
        it.author ?: "",
        it.title ?: "",
        it.description ?: "",
        it.url ?: "",
        it.urlToImage ?: "",
        it.publishedAt ?: "",
        it.content ?: ""
    )
}