package io.github.sainiharry.meteor.weatherrepository

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
import org.koin.dsl.module

private const val DATABASE_NAME = "WeatherDb"

private const val UNIT_METRIC = "metric"

/**
 * Koin module for providing single instance of WeatherRepository. Injecting components should pass
 * applicationContext when injecting weatherRepository
 */
val weatherRepositoryModule = module {
    single<WeatherRepository> {
        val weatherApi = WeatherApi(BuildConfig.WEATHER_API_KEY)
        val retrofit = NetworkInteractor.getRetrofit(weatherApi)
        val openWeatherService = retrofit.create(OpenWeatherService::class.java)

        WeatherRepositoryImpl(
            openWeatherService,
            Room.databaseBuilder(get(), WeatherDatabase::class.java, DATABASE_NAME).build(),
            get()
        )
    }
}

/**
 * Repository for accessing data related to weather
 */
interface WeatherRepository {

    /**
     * Get current weather data for a city
     * @param cityName city for which weather data is required
     * @return weather data for the specified city
     */
    suspend fun fetchCurrentWeather(cityName: String): Weather

    /**
     * Get current weather data for a city
     * @param lat latitude of city for which weather data is required
     * @param lon longitude of city for which weather data is required
     * @return weather data for the specified location
     */
    suspend fun fetchCurrentWeather(lat: Double, lon: Double): Weather

    /**
     * Get listener for changes in current weather data for a city
     * @param cityName city for which weather data needs to be observed
     * @return a [LiveData] that starts emitting latest current weather data
     */
    fun getCurrentWeatherListener(cityName: String): LiveData<Weather>

    /**
     * Get forecast weather data for a city
     * @param cityName city for which forecast data is required
     * @return forecast data
     */
    suspend fun fetchForecast(cityName: String): List<Weather>

    /**
     * Get listener for changes in forecast data for a city
     * @param cityName city for which forecast data needs to be observed
     * @return a [LiveData] that starts emitting latest forecast data changes when observed
     */
    fun getForecastListener(cityName: String): LiveData<List<Weather>>
}

/**
 * Internal implementation of WeatherRepository
 */
internal class WeatherRepositoryImpl(
    private val openWeatherService: OpenWeatherService,
    private val weatherDatabase: WeatherDatabase,
    private val searchRepository: SearchRepository
) : WeatherRepository {

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
        val forecastResponse = openWeatherService.getForecast(cityName, UNIT_METRIC)
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
            searchRepository.handleSearchQuery(cityName)
        }

        return weather
    }
}