package io.github.sainiharry.meteor.weatherrepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.weatherrepository.database.flatten
import io.github.sainiharry.meteor.weatherrepository.network.WeatherResponse
import io.github.sainiharry.meteor.weatherrepository.network.OpenWeatherService
import io.github.sainiharry.meteor.weatherrepository.network.WeatherApi
import io.github.sainiharry.meteor.weatherrepository.network.flatten
import io.github.sainiharry.meteor.network.NetworkInteractor
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

    fun fetchCurrentWeather(lat: Double, lng: Double): Single<Weather>

    fun getCurrentWeatherListener(cityName: String): LiveData<Weather>
}

internal class WeatherRepositoryImpl(
    private val openWeatherService: OpenWeatherService,
    private val weatherDatabase: WeatherDatabase
) :
    WeatherRepository {

    override fun fetchCurrentWeather(cityName: String): Single<Weather> =
        openWeatherService.getCurrentWeather(cityName)
            .map(WeatherResponse::flatten)
            .doOnSuccess { weather ->
                weatherDatabase.weatherDao().insertWeather(WeatherModel(weather))
            }

    override fun fetchCurrentWeather(lat: Double, lng: Double): Single<Weather> =
        openWeatherService.getCurrentWeather(lat, lng)
            .map(WeatherResponse::flatten)
            .doOnSuccess { weather ->
                weatherDatabase.weatherDao().insertWeather(WeatherModel(weather))
            }

    override fun getCurrentWeatherListener(cityName: String): LiveData<Weather> =
        Transformations.map(
            weatherDatabase.weatherDao().getCurrentWeatherListener(cityName),
            WeatherModel::flatten
        )
}