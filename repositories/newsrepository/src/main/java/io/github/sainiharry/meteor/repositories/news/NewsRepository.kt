package io.github.sainiharry.meteor.repositories.news

import android.content.Context
import androidx.room.Room
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.github.sainiharry.meteor.repositories.news.database.NewsDatabase
import io.github.sainiharry.meteor.repositories.news.network.NewsApi
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNewsModelList

private const val DATABASE_NAME = "NewsDb"

/**
 * Returns cached version of [NewsRepository] implementation
 * @param applicationContext applicationContext
 */
fun getNewsRepository(
    applicationContext: Context
): NewsRepository = NewsRepository.getInstance(
    newsServiceProvider = {
        val weatherApi = NewsApi(BuildConfig.NEWS_API_KEY)
        val retrofit = NetworkInteractor.getRetrofit(weatherApi)
        retrofit.create(NewsService::class.java)
    },
    newsDatabaseProvider = {
        Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java,
            DATABASE_NAME
        ).build()
    })

/**
 * Repository for accessing data related to news
 */
interface NewsRepository {

    companion object {

        private var newsRepository: NewsRepository? = null

        internal fun getInstance(
            newsServiceProvider: () -> NewsService,
            newsDatabaseProvider: () -> NewsDatabase
        ): NewsRepository = newsRepository ?: NewsRepositoryImpl(
            newsServiceProvider(),
            newsDatabaseProvider()
        ).also {
            newsRepository = it
        }
    }

    /**
     * Get top headlines for a country
     * @param countryCode country code for which news data is required
     * @return news articles list for the specified country
     */
    suspend fun fetchNews(countryCode: String): List<News>
}

internal class NewsRepositoryImpl(
    private val newsService: NewsService,
    newsDatabase: NewsDatabase
) : NewsRepository {

    private val newsDao = newsDatabase.newsDao()

    override suspend fun fetchNews(countryCode: String): List<News> {
        val networkResponse = newsService.fetchNews(countryCode)
        val articles = networkResponse.articles
        newsDao.clearAndInsertNews(articles.toNewsModelList())
        return newsDao.getAllNews()
    }
}