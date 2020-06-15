package io.github.sainiharry.meteor.weatherrepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.database.flatten
import io.github.sainiharry.meteor.weatherrepository.network.*
import io.github.sainiharry.meteor.weatherrepository.network.toWeather
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

    fun fetchForecast(cityName: String): Single<List<Weather>>

    fun getForecastListener(cityName: String): LiveData<List<Weather>>
}

internal class WeatherRepositoryImpl(
    private val openWeatherService: OpenWeatherService,
    private val weatherDatabase: WeatherDatabase
) :
    WeatherRepository {

    override fun fetchCurrentWeather(cityName: String): Single<Weather> =
        openWeatherService.getCurrentWeather(cityName)
            .map(WeatherResponse::toWeather)
            .doOnSuccess { weather ->
                weatherDatabase.weatherDao().insertWeather(WeatherModel(weather))
            }

    override fun fetchCurrentWeather(lat: Double, lng: Double): Single<Weather> =
        openWeatherService.getCurrentWeather(lat, lng)
            .map(WeatherResponse::toWeather)
            .doOnSuccess { weather ->
                weatherDatabase.weatherDao().insertWeather(WeatherModel(weather))
            }

    override fun getCurrentWeatherListener(cityName: String): LiveData<Weather> =
        Transformations.map(
            weatherDatabase.weatherDao().getCurrentWeatherListener(cityName),
            WeatherModel::flatten
        )

    override fun fetchForecast(cityName: String): Single<List<Weather>> =
        openWeatherService.getForecast(cityName, 3)
            .map(ForecastResponse::toWeatherList)
            .doOnSuccess { forecastList ->
                weatherDatabase.weatherDao().insertForecast(forecastList.toForecastModelList())
            }

    override fun getForecastListener(cityName: String): LiveData<List<Weather>> =
        Transformations.map(
            weatherDatabase.weatherDao().getForecastListener(cityName)
        ) { forecastModelList ->
            forecastModelList.map { it.flatten() }
        }
}