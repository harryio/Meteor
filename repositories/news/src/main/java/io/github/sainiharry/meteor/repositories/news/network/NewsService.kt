package io.github.sainiharry.meteor.repositories.news.network

import io.reactivex.Single
import retrofit2.http.GET

interface NewsService {

    @GET("top-headlines")
    fun fetchNews(): Single<List<NewsResponse>>
}