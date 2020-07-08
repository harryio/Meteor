package io.github.sainiharry.meteor.repositories.news

import androidx.room.Room
import io.github.sainiharry.meteor.common.model.News
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.github.sainiharry.meteor.repositories.news.database.NewsDatabase
import io.github.sainiharry.meteor.repositories.news.network.NewsApi
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNewsModelList
import org.koin.dsl.module

private const val DATABASE_NAME = "NewsDb"

/**
 * Koin module for providing single instance of NewsRepository. Injecting components should pass
 * applicationContext when injecting newsRepository
 */
val newsRepositoryModule = module {
    single<NewsRepository> {
        val newsApi = NewsApi(BuildConfig.NEWS_API_KEY)
        val retrofit = NetworkInteractor.getRetrofit(newsApi)
        val newsService = retrofit.create(NewsService::class.java)

        NewsRepositoryImpl(
            newsService,
            Room.databaseBuilder(get(), NewsDatabase::class.java, DATABASE_NAME).build()
        )
    }
}

/**
 * Repository for accessing data related to news
 */
interface NewsRepository {

    /**
     * Get top headlines for a country
     * @param countryCode country code for which news data is required
     * @return news articles list for the specified country
     */
    suspend fun fetchNews(countryCode: String): List<News>
}

/**
 * Internal implementation of NewsRepository
 */
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