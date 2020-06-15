package io.github.sainiharry.meteor.weatherrepository.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OpenWeatherService {

    @GET("2.5/weather")
    fun getCurrentWeather(@Query("q") cityName: String): Single<WeatherResponse>

    @GET("2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<WeatherResponse>

    @GET("2.5/forecast")
    fun getForecast(
        @Query("q") cityName: String,
        @Query("cnt") count: Int
    ): Single<ForecastResponse>
}