package io.github.sainiharry.meteor.repositories.news.network

import io.github.sainiharry.meteor.network.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val NEWS_BASE_API_URL = "https://newsapi.org/v2/"

/**
 * Implementation of [Api] for news data
 */
internal class NewsApi(private val apiKey: String) : Api {

    private val retrofitBuilder by lazy {
        Retrofit.Builder().baseUrl(NEWS_BASE_API_URL)
    }

    override fun buildRetrofit(): Retrofit.Builder = retrofitBuilder

    override fun buildOkHttpClient(okHttpBuilder: OkHttpClient.Builder): OkHttpClient.Builder =
        okHttpBuilder.addInterceptor(
            NewsApiKeyInterceptor(
                apiKey
            )
        )
}