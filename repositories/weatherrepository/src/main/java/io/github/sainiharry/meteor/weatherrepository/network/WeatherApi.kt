package io.github.sainiharry.meteor.weatherrepository.network

import io.github.sainiharry.meteor.network.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private const val WEATHER_BASE_API_URL = "https://api.openweathermap.org/data/"

/**
 * Implementation of [Api] for weather data
 */
internal class WeatherApi(private val apiKey: String) : Api {

    private val retrofitBuilder by lazy {
        Retrofit.Builder().baseUrl(WEATHER_BASE_API_URL)
    }

    override fun buildRetrofit(): Retrofit.Builder = retrofitBuilder

    override fun buildOkHttpClient(okHttpBuilder: OkHttpClient.Builder): OkHttpClient.Builder =
        okHttpBuilder.addInterceptor(
            WeatherApiKeyInterceptor(
                apiKey
            )
        )
}