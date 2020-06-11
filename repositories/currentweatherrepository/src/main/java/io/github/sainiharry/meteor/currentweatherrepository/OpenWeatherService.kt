package io.github.sainiharry.meteor.currentweatherrepository

import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Single

internal interface OpenWeatherService {

    @GET("/2.5/weather")
    fun getCurrentWeather(@Query("q") cityName: String): Single<CurrentWeatherResponse>
}