package io.github.sainiharry.meteor.weatherrepository.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Network Service that provides weather data from [OpenWeatherMap](https://openweathermap.org/api)
 */
internal interface OpenWeatherService {

    /**
     * Get current weather data for a city from network
     * @param cityName city for which weather data is required
     * @return a [Single] that emits network response when subscribed
     */
    @GET("2.5/weather")
    fun getCurrentWeather(@Query("q") cityName: String): Single<WeatherResponse>

    /**
     * Get current weather data for a city from network
     * @param lat latitude of city for which weather data is required
     * @param lon longitude of city for which weather data is required
     * @return a [Single] that emits network response when subscribed
     */
    @GET("2.5/weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<WeatherResponse>

    /**
     * Get forecast weather data for a city from network
     * @param cityName city for which forecast data is required
     * @param count number of results to be requested from api
     * @return a [Single] that emits network response when subscribed
     */
    @GET("2.5/forecast")
    fun getForecast(
        @Query("q") cityName: String,
        @Query("cnt") count: Int
    ): Single<ForecastResponse>
}