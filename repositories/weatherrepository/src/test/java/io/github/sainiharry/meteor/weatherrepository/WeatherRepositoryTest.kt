package io.github.sainiharry.meteor.weatherrepository

import io.github.sainiharry.meteor.weatherrepository.database.WeatherDao
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.network.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    @Mock
    private lateinit var weatherDatabase: WeatherDatabase

    @Mock
    private lateinit var openWeatherService: OpenWeatherService

    @Mock
    private lateinit var weatherDao: WeatherDao

    private lateinit var weatherRepository: WeatherRepository

    private val cityName = "London"

    private val location = 23.3 to 48.3

    @Before
    fun setup() {
        `when`(weatherDatabase.weatherDao()).thenReturn(weatherDao)
        weatherRepository = WeatherRepositoryImpl(openWeatherService, weatherDatabase)
    }

    @Test
    fun testFetchCurrentWeatherForCity() {
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.toWeather()
        val currentWeatherModel = WeatherModel(weather)
        `when`(openWeatherService.getCurrentWeather(cityName)).thenReturn(
            Single.just(
                mockWeatherResponse
            )
        )

        val subscription = weatherRepository.fetchCurrentWeather(cityName).test()
        subscription.assertNoErrors()
        subscription.assertValue(weather)

        verify(openWeatherService).getCurrentWeather(cityName)
        verify(weatherDao).insertWeather(currentWeatherModel)
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    @Test
    fun testFetchWeatherForLocation() {
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.toWeather()
        val currentWeatherModel = WeatherModel(weather)
        `when`(openWeatherService.getCurrentWeather(location.first, location.second)).thenReturn(
            Single.just(
                mockWeatherResponse
            )
        )

        val subscription =
            weatherRepository.fetchCurrentWeather(location.first, location.second).test()
        subscription.assertNoErrors()
        subscription.assertValue(weather)

        verify(openWeatherService).getCurrentWeather(location.first, location.second)
        verify(weatherDao).insertWeather(currentWeatherModel)
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    @Test
    fun testFetchForecastForCity() {
        val mockForecastResponse = mockForecastResponse()
        val weatherList = mockForecastResponse.toWeatherList()
        `when`(openWeatherService.getForecast(cityName)).thenReturn(Single.just(mockForecastResponse))

        val subscription = weatherRepository.fetchForecast(cityName).test()
        subscription.assertNoErrors()
        subscription.assertValue(weatherList)

        verify(openWeatherService).getForecast(cityName)
        verify(weatherDao).insertForecast(weatherList.toForecastModelList())
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    @Test
    fun testFetchForecastForLocation() {
        val mockForecastResponse = mockForecastResponse()
        val weatherList = mockForecastResponse.toWeatherList()
        `when`(
            openWeatherService.getForecast(
                location.first,
                location.second
            )
        ).thenReturn(Single.just(mockForecastResponse))

        val subscription = weatherRepository.fetchForecast(location.first, location.second).test()
        subscription.assertNoErrors()
        subscription.assertValue(weatherList)

        verify(openWeatherService).getForecast(location.first, location.second)
        verify(weatherDao).insertForecast(weatherList.toForecastModelList())
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
                WeatherConditionResponse(cityName.hashCode().toLong(), "Clear", "04d"),
                System.currentTimeMillis()
            )
        ),
        ForecastWeatherLocation(cityName.hashCode().toLong(), cityName, "GB")
    )
}