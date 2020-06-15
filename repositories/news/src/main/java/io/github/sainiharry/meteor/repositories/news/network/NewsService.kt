package io.github.sainiharry.meteor.repositories.news.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Network Service that provides news data from [NewsApi](https://newsapi.org/docs)
 */
internal interface NewsService {

    /**
     * Get top headlines for a country from network
     * @param country country for which news data is to be fetched
     * @return a [Single] which will emit network response when subscribed
     */
    @GET("top-headlines")
    fun fetchNews(@Query("country") country: String): Single<NewsResponseWrapper>
}