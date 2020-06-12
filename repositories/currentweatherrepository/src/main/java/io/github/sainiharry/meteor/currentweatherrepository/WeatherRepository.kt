package io.github.sainiharry.meteor.currentweatherrepository

import android.content.Context
import androidx.room.Room
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.currentweatherrepository.database.CurrentWeatherModel
import io.github.sainiharry.meteor.currentweatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.currentweatherrepository.database.flatten
import io.github.sainiharry.meteor.currentweatherrepository.network.CurrentWeatherResponse
import io.github.sainiharry.meteor.currentweatherrepository.network.OpenWeatherService
import io.github.sainiharry.meteor.currentweatherrepository.network.WeatherApi
import io.github.sainiharry.meteor.currentweatherrepository.network.flatten
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single

private const val DATABASE_NAME = "WeatherDb"

fun getWeatherRepository(
    scheduler: Scheduler,
    applicationContext: Context
): WeatherRepository = WeatherRepository.getInstance(
    openWeatherServiceProvider = {
        val weatherApi = WeatherApi(BuildConfig.WEATHER_API_KEY)
        val retrofit = NetworkInteractor.getRetrofit(weatherApi, scheduler)
        retrofit.create(OpenWeatherService::class.java)
    },
    weatherDatabaseProvider = {
        Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            DATABASE_NAME
        ).build()
    })

interface WeatherRepository {

    companion object {

        private var weatherRepository: WeatherRepository? = null

        internal fun getInstance(
            openWeatherServiceProvider: () -> OpenWeatherService,
            weatherDatabaseProvider: () -> WeatherDatabase
        ): WeatherRepository {
            if (weatherRepository == null) {
                weatherRepository =
                    WeatherRepositoryImpl(openWeatherServiceProvider(), weatherDatabaseProvider())
            }

            return weatherRepository!!
        }
    }

    fun fetchCurrentWeather(cityName: String): Single<Weather>

    fun getCurrentWeatherListener(cityName: String): Flowable<Weather>
}

internal class WeatherRepositoryImpl(
    private val openWeatherService: OpenWeatherService,
    private val weatherDatabase: WeatherDatabase
) :
    WeatherRepository {

    override fun fetchCurrentWeather(cityName: String): Single<Weather> =
        openWeatherService.getCurrentWeather(cityName)
            .map(CurrentWeatherResponse::flatten)
            .doOnSuccess { weather ->
                weatherDatabase.weatherDao().insertCurrentWeather(CurrentWeatherModel(weather))
            }

    override fun getCurrentWeatherListener(cityName: String): Flowable<Weather> =
        weatherDatabase.weatherDao().getCurrentWeather(cityName)
            .map(CurrentWeatherModel::flatten)
}