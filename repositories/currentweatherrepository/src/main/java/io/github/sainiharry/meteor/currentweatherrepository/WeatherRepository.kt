package io.github.sainiharry.meteor.currentweatherrepository

import io.github.sainiharry.meteor.common.Weather
import io.reactivex.Single

interface WeatherRepository {

    fun getCurrentWeather(cityName: String): Single<Weather>
}

internal class WeatherRepositoryImpl(val openWeatherService: OpenWeatherService) :
    WeatherRepository {

    override fun getCurrentWeather(cityName: String): Single<Weather> {
        TODO("Not yet implemented")
    }
}