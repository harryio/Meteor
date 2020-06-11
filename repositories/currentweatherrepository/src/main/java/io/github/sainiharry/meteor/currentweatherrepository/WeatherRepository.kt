package io.github.sainiharry.meteor.currentweatherrepository

import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.reactivex.Scheduler
import io.reactivex.Single

interface WeatherRepository {

    companion object {

        private var weatherRepository: WeatherRepository? = null

        fun getInstance(scheduler: Scheduler): WeatherRepository {
            if (weatherRepository == null) {
                val weatherApi = WeatherApi(BuildConfig.WEATHER_API_KEY)
                val retrofit = NetworkInteractor.getRetrofit(weatherApi, scheduler)
                val openWeatherService = retrofit.create(OpenWeatherService::class.java)
                weatherRepository = WeatherRepositoryImpl(openWeatherService)
            }

            return weatherRepository!!
        }
    }

    fun getCurrentWeather(cityName: String): Single<Weather>
}

internal class WeatherRepositoryImpl(val openWeatherService: OpenWeatherService) :
    WeatherRepository {

    override fun getCurrentWeather(cityName: String): Single<Weather> =
        openWeatherService.getCurrentWeather(cityName).map { it.flatten() }
}