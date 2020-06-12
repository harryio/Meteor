package io.github.sainiharry.meteor.currentweatherrepository.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OpenWeatherService {

    @GET("2.5/weather")
    fun getCurrentWeather(@Query("q") cityName: String): Single<CurrentWeatherResponse>

    @GET("2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<CurrentWeatherResponse>
}