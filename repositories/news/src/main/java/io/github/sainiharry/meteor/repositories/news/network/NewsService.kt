package io.github.sainiharry.meteor.repositories.news.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NewsService {

    @GET("top-headlines")
    fun fetchNews(@Query("country") country: String): Single<NewsResponseWrapper>
}