package io.github.sainiharry.meteor.weatherrepository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.network.NetworkInteractor
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.database.toWeather
import io.github.sainiharry.meteor.weatherrepository.network.*
import io.github.sainiharry.searchrepository.SearchRepository
import io.github.sainiharry.searchrepository.getSearchRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DATABASE_NAME = "WeatherDb"

private const val UNIT_METRIC = "metric"

/**
 * Returns cached version of [WeatherRepository] implementation
 * @param scheduler [Scheduler] on which data should be gathered on
 * @param applicationContext applicationContext
 */
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
    },
    searchRepositoryProvider = {
        getSearchRepository(applicationContext)
    }
)

/**
 * Repository for accessing data related to weather
 */
interface WeatherRepository {

    companion object {

        private var weatherRepository: WeatherRepository? = null

        internal fun getInstance(
            openWeatherServiceProvider: () -> OpenWeatherService,
            weatherDatabaseProvider: () -> WeatherDatabase,
            searchRepositoryProvider: () -> SearchRepository
        ): WeatherRepository = weatherRepository ?: WeatherRepositoryImpl(
            openWeatherServiceProvider(),
            weatherDatabaseProvider(),
            searchRepositoryProvider()
        ).also {
            weatherRepository = it
        }
    }

    /**
     * Get current weather data for a city
     * @param cityName city for which weather data is required
     * @return a [Single] that emits the weather data for the specified city when subscribed
     */
    suspend fun fetchCurrentWeather(cityName: String): Weather

    /**
     * Get current weather data for a city
     * @param lat latitude of city for which weather data is required
     * @param lon longitude of city for which weather data is required
     * @return a [Single] that emits weather data for the specified location when subscribed
     */
    suspend fun fetchCurrentWeather(lat: Double, lon: Double): Weather

    /**
     * Get listener for changes in current weather data for a city
     * @param cityName city for which weather data needs to be observed
     * @return a [LiveData] that starts emitting latest current weather data when observed
     */
    fun getCurrentWeatherListener(cityName: String): LiveData<Weather>

    /**
     * Get forecast weather data for a city
     * @param cityName city for which forecast data is required
     * @return a [Single] that emits forecast data when subscribed
     */
    suspend fun fetchForecast(cityName: String): List<Weather>

    /**
     * Get listener for changes in forecast data for a city
     * @param cityName city for which forecast data needs to be observed
     * @return a [LiveData] that starts emitting latest forecast data changes when observed
     */
    fun getForecastListener(cityName: String): LiveData<List<Weather>>
}

internal class WeatherRepositoryImpl(
    private val openWeatherService: OpenWeatherService,
    private val weatherDatabase: WeatherDatabase,
    private val searchRepository: SearchRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    WeatherRepository {

    override suspend fun fetchCurrentWeather(cityName: String): Weather =
        handleWeatherResponse(openWeatherService.getCurrentWeather(cityName, UNIT_METRIC), cityName)

    override suspend fun fetchCurrentWeather(lat: Double, lon: Double): Weather =
        handleWeatherResponse(openWeatherService.getCurrentWeather(lat, lon, UNIT_METRIC))

    override fun getCurrentWeatherListener(cityName: String): LiveData<Weather> =
        Transformations.map(
            weatherDatabase.weatherDao().getCurrentWeatherListener(cityName),
            WeatherModel::toWeather
        )

    override suspend fun fetchForecast(cityName: String): List<Weather> {
        val forecastResponse = openWeatherService.getForecast(cityName, 3, UNIT_METRIC)
        weatherDatabase.weatherDao().insertForecast(forecastResponse.toForecastModelList())
        return forecastResponse.toWeatherList()
    }

    override fun getForecastListener(cityName: String): LiveData<List<Weather>> =
        weatherDatabase.weatherDao().getForecastListener(cityName)

    private suspend fun handleWeatherResponse(
        weatherResponse: WeatherResponse,
        cityName: String? = null
    ): Weather {
        val weather = weatherResponse.toWeather()
        weatherDatabase.weatherDao().insertWeather(WeatherModel(weather))
        cityName?.let {
            withContext(dispatcher) {
                searchRepository.handleSearchQuery(cityName)
            }
        }

        return weather
    }
}