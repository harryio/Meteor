package io.github.sainiharry.meteor.weatherrepository.network

import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY_QUERY_PARAM = "appid"

/**
 * [Interceptor] that adds weather api key as query parameter to every network request
 * initiated from [WeatherApi]
 */
internal class WeatherApiKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter(API_KEY_QUERY_PARAM, apiKey)
            .build()

        return chain.proceed(originalRequest.newBuilder().url(newUrl).build())
    }
}