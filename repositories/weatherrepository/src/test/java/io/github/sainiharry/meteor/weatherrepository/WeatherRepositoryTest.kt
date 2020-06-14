package io.github.sainiharry.meteor.weatherrepository

import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.weatherrepository.database.WeatherModel
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDao
import io.github.sainiharry.meteor.weatherrepository.database.WeatherDatabase
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

    val cityName = "London"

    @Before
    fun setup() {
        `when`(weatherDatabase.weatherDao()).thenReturn(weatherDao)
        weatherRepository = WeatherRepositoryImpl(openWeatherService, weatherDatabase)
    }

    @Test
    fun testFetchCurrentWeatherForCity() {
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.flatten()
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
        val location = 23.3 to 48.3
        val mockWeatherResponse = mockWeatherResponse()
        val weather = mockWeatherResponse.flatten()
        val currentWeatherModel = WeatherModel(weather)
        `when`(openWeatherService.getCurrentWeather(location.first, location.second)).thenReturn(
            Single.just(
                mockWeatherResponse
            )
        )

        val subscription = weatherRepository.fetchCurrentWeather(location.first, location.second).test()
        subscription.assertNoErrors()
        subscription.assertValue(weather)

        verify(openWeatherService).getCurrentWeather(location.first, location.second)
        verify(weatherDao).insertWeather(currentWeatherModel)
        verifyNoMoreInteractions(openWeatherService)
        verifyNoMoreInteractions(weatherDao)
    }

    private fun mockWeatherResponse() = WeatherResponse(
        cityName.hashCode().toLong(),
        cityName,
        listOf(WeatherConditionResponse(cityName.hashCode().toLong(), "Clear", "04d")),
        WeatherInfoResponse(23.2f, 43.2f, 85.3f),
        WeatherSysResponse("US")
    )

    private fun mockWeather(
        cityName: String = "Montreal",
        id: Long = cityName.hashCode().toLong(),
        main: String = "Clear",
        icon: String = "0445",
        cityId: Long = cityName.hashCode().toLong(),
        temp: Float = 38.3f,
        maxTemp: Float = 30.3f,
        minTemp: Float = 43.1f,
        country: String = "US"
    ) = Weather(id, main, icon, cityId, cityName, temp, maxTemp, minTemp, country)
}