package io.github.sainiharry.meteor.weatherrepository

import io.github.sainiharry.meteor.weatherrepository.database.WeatherDao
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.network.*
import io.github.sainiharry.searchrepository.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    @Mock
    private lateinit var weatherDatabase: WeatherDatabase

    @Mock
    private lateinit var openWeatherService: OpenWeatherService

    @Mock
    private lateinit var weatherDao: WeatherDao

    @Mock
    private lateinit var searchRepository: SearchRepository

    private lateinit var weatherRepository: WeatherRepository

    private val cityName = "London"

    private val metric = "metric"

    private val location = 23.3 to 48.3

    @Before
    fun setup() {
        `when`(weatherDatabase.weatherDao()).thenReturn(weatherDao)
        weatherRepository =
            WeatherRepositoryImpl(
                openWeatherService,
                weatherDatabase,
                searchRepository
            )
    }

    @Test
    fun testFetchCurrentWeatherForCity() = runBlockingTest {
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.toWeather()
        val currentWeatherModel = WeatherModel(weather)
        `when`(openWeatherService.getCurrentWeather(cityName, metric)).thenReturn(
            mockWeatherResponse
        )

        val responseWeather = weatherRepository.fetchCurrentWeather(cityName)
        assert(responseWeather == weather)
        verify(openWeatherService).getCurrentWeather(cityName, metric)
        verify(weatherDao).insertWeather(currentWeatherModel)
        verify(searchRepository).handleSearchQuery(cityName)
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
        verifyNoMoreInteractions(searchRepository)
    }

    @Test
    fun testFetchWeatherForLocation() = runBlockingTest {
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.toWeather()
        val currentWeatherModel = WeatherModel(weather)
        `when`(
            openWeatherService.getCurrentWeather(
                location.first,
                location.second,
                metric
            )
        ).thenReturn(mockWeatherResponse)

        val responseWeather = weatherRepository.fetchCurrentWeather(location.first, location.second)
        assert(responseWeather == weather)

        verify(openWeatherService).getCurrentWeather(location.first, location.second, metric)
        verify(weatherDao).insertWeather(currentWeatherModel)
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    @Test
    fun testFetchForecastForCity() = runBlockingTest {
        val mockForecastResponse = mockForecastResponse()
        val weatherList = mockForecastResponse.toWeatherList()
        `when`(openWeatherService.getForecast(cityName, metric)).thenReturn(
            mockForecastResponse
        )

        val forecastWeather = weatherRepository.fetchForecast(cityName)
        assert(forecastWeather == weatherList)
        verify(openWeatherService).getForecast(cityName, metric)
        verify(weatherDao).insertForecast(mockForecastResponse.toForecastModelList())
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    private fun mockWeatherResponse() = WeatherResponse(
        cityName.hashCode().toLong(),
        cityName,
        listOf(WeatherConditionResponse(cityName.hashCode().toLong(), "Clear", "04d")),
        WeatherInfoResponse(23.2f, 43.2f, 85.3f),
        WeatherSysResponse("US"),
        System.currentTimeMillis()
    )

    private fun mockForecastResponse() = ForecastResponse(
        listOf(
            ForecastWeather(
                WeatherInfoResponse(
                    23.2f, 43.2f, 85.3f
                ),
                listOf(
                    WeatherConditionResponse(cityName.hashCode().toLong(), "Clear", "04d")
                ),
                System.currentTimeMillis(),
                "2020-07-01"
            )
        ),
        ForecastWeatherLocation(cityName.hashCode().toLong(), cityName, "GB")
    )
}