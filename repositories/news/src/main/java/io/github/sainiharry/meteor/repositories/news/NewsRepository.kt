package io.github.sainiharry.meteor.repositories.news

import android.content.Context
import androidx.room.Room
import io.github.sainiharry.meteor.common.News
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.github.sainiharry.meteor.repositories.news.database.NewsDatabase
import io.github.sainiharry.meteor.repositories.news.network.NewsApi
import io.github.sainiharry.meteor.repositories.news.network.NewsService
import io.github.sainiharry.meteor.repositories.news.network.toNewsModelList
import io.reactivex.Scheduler
import io.reactivex.Single

private const val DATABASE_NAME = "NewsDb"

fun getNewsRepository(
    scheduler: Scheduler,
    applicationContext: Context
): NewsRepository = NewsRepository.getInstance(
    newsServiceProvider = {
        val weatherApi = NewsApi(BuildConfig.NEWS_API_KEY)
        val retrofit = NetworkInteractor.getRetrofit(weatherApi, scheduler)
        retrofit.create(NewsService::class.java)
    },
    newsDatabaseProvider = {
        Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java,
            DATABASE_NAME
        ).build()
    })


interface NewsRepository {

    companion object {

        private var newsRepository: NewsRepository? = null

        internal fun getInstance(
            newsServiceProvider: () -> NewsService,
            newsDatabaseProvider: () -> NewsDatabase
        ): NewsRepository {
            if (newsRepository == null) {
                newsRepository =
                    NewsRepositoryImpl(newsServiceProvider(), newsDatabaseProvider())
            }

            return newsRepository!!
        }
    }

    fun fetchNews(countryCode: String): Single<List<News>>
}

internal class NewsRepositoryImpl(
    private val newsService: NewsService,
    private val newsDatabase: NewsDatabase
) : NewsRepository {

    override fun fetchNews(countryCode: String): Single<List<News>> =
        newsService.fetchNews(countryCode)
            .map { it.articles }
            .doOnSuccess {
                newsDatabase.newsDao().clearAndInsertNews(it.toNewsModelList())
            }
            .flatMap { newsDatabase.newsDao().getAllNews() }
}