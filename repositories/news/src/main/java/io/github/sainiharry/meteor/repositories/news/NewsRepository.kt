package io.github.sainiharry.meteor.repositories.news

import androidx.lifecycle.LiveData
import io.github.sainiharry.meteor.repositories.news.database.NewsDao
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNews
import io.reactivex.Single

interface NewsRepository {

    fun fetchNews(countryCode: String): Single<List<News>>

    fun getNewsListener(): LiveData<List<News>>
}

internal class NewsRepositoryImpl(
    private val newsService: NewsService,
    private val newsDao: NewsDao
) : NewsRepository {

    override fun fetchNews(countryCode: String): Single<List<News>> =
        newsService.fetchNews()
            .doOnSuccess {
                newsDao.clear()
            }
            .map {
                it.toNews()
            }
            .doOnSuccess(newsDao::insertNews)

    override fun getNewsListener(): LiveData<List<News>> = newsDao.getNewsListener()
}