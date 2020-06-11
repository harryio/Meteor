package io.github.sainiharry.meteor.currentweatherrepository.network

import io.github.sainiharry.meteor.network.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val WEATHER_BASE_API_URL = "http://api.openweathermap.org/data"

internal class WeatherApi(private val apiKey: String) : Api {

    override fun buildRetrofit(retrofitBuilder: Retrofit.Builder): Retrofit.Builder =
        retrofitBuilder.baseUrl(WEATHER_BASE_API_URL)

    override fun buildOkHttpClient(okHttpBuilder: OkHttpClient.Builder): OkHttpClient.Builder =
        okHttpBuilder.addInterceptor(
            WeatherApiKeyInterceptor(
                apiKey
            )
        )
}