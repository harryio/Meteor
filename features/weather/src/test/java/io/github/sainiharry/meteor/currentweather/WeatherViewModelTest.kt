package io.github.sainiharry.meteor.currentweather

import android.accounts.NetworkErrorException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.commonfeature.captureValues
import io.github.sainiharry.meteor.commonfeature.getValueForTest
import io.github.sainiharry.meteor.currentweatherrepository.WeatherRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class WeatherViewModelTest {

    private lateinit var weatherRepository: MockWeatherRepository

    private lateinit var testScheduler: Scheduler

    private lateinit var weatherViewModel: WeatherViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        weatherRepository = MockWeatherRepository()
        testScheduler = Schedulers.trampoline()
        weatherViewModel = WeatherViewModel(weatherRepository, testScheduler)
    }

    @Test
    fun testInvalidCityName() {
        weatherViewModel.weather.captureValues {
            weatherViewModel.handleUserQuery(null)
            weatherViewModel.handleUserQuery("")
            weatherViewModel.handleUserQuery("          ")

            assertTrue(values.isEmpty())
        }
    }

    @Test
    fun testWeatherEvents() {
        weatherViewModel.weather.captureValues {
            val cityName = "London"
            weatherViewModel.handleUserQuery(cityName)

            val weather1 = weatherRepository.mockWeather(cityName)
            val weather2 = weatherRepository.mockWeather(cityName, temp = 32.1f)
            weatherRepository.getWeatherPusher(cityName).value = weather1
            weatherRepository.getWeatherPusher(cityName).value = weather2

            assertEquals(2, values.size)
            assertEquals(weather1, values[0])
            assertEquals(weather2, values[1])
        }
    }

    @Test
    fun testThatOnlyLatestWeatherWillBeShown() {
        val cityName = "London"
        weatherViewModel.handleUserQuery(cityName)

        val weather = weatherRepository.mockWeather(cityName, temp = 32.1f)
        weatherRepository.getWeatherPusher(cityName).value = weatherRepository.mockWeather(cityName)
        weatherRepository.getWeatherPusher(cityName).value = weather

        assertEquals(weather, weatherViewModel.weather.getValueForTest())
    }

    @Test
    fun testCityChange() {
        val city1 = "London"
        val city2 = "Paris"

        val city1WeatherPusher = weatherRepository.getWeatherPusher(city1)
        val city2WeatherPusher = weatherRepository.getWeatherPusher(city2)

        weatherViewModel.handleUserQuery(city1)
        val city1Weather = weatherRepository.mockWeather(city1)
        city1WeatherPusher.value = city1Weather
        assertEquals(city1Weather, weatherViewModel.weather.getValueForTest())

        weatherViewModel.handleUserQuery(city2)
        val city2Weather = weatherRepository.mockWeather(city2)
        city2WeatherPusher.value = city2Weather
        assertEquals(city2Weather, weatherViewModel.weather.getValueForTest())

        city1WeatherPusher.value = weatherRepository.mockWeather(city1, temp = 72.2f)
        city1WeatherPusher.value = weatherRepository.mockWeather(city1, temp = 21.3f)
        city1WeatherPusher.value = weatherRepository.mockWeather(city1, temp = 32.3f)
        assertEquals(city2Weather, weatherViewModel.weather.getValueForTest())
    }

    @Test
    fun testLoading() {
        weatherViewModel.loading.captureValues {
            val cityName = "London"
            weatherViewModel.handleUserQuery(cityName)

            val weather1 = weatherRepository.mockWeather(cityName)
            val weather2 = weatherRepository.mockWeather(cityName, temp = 32.1f)
            weatherRepository.getWeatherPusher(cityName).value = weather1
            weatherRepository.getWeatherPusher(cityName).value = weather2

            assertEquals(2, values.size)
            assertEquals(Event(true), values[0])
            assertEquals(Event(false), values[1])

            weatherViewModel.handleUserQuery("New York")
            assertEquals(4, values.size)
            assertEquals(Event(true), values[2])
            assertEquals(Event(false), values[3])
        }
    }

    @Test
    fun testError() {
        weatherViewModel.error.captureValues {
            weatherRepository.error = true

            weatherViewModel.handleUserQuery("London")

            assertEquals(1, values.size)
            assertEquals(Event(R.string.error_current_weather_fetch), values[0])
        }
    }
}

internal class MockWeatherRepository : WeatherRepository {

    var error = false

    private val weatherPusherMap = mutableMapOf<String, MutableLiveData<Weather>>()

    fun mockWeather(
        cityName: String,
        id: Long = cityName.hashCode().toLong(),
        main: String = "Clear",
        icon: String = "0445",
        cityId: Long = cityName.hashCode().toLong(),
        temp: Float = 38.3f,
        maxTemp: Float = 30.3f,
        minTemp: Float = 43.1f,
        country: String = "US"
    ) = Weather(id, main, icon, cityId, cityName, temp, maxTemp, minTemp, country)

    fun getWeatherPusher(cityName: String): MutableLiveData<Weather> {
        var weatherPusher = weatherPusherMap[cityName]
        if (weatherPusher == null) {
            weatherPusher = MutableLiveData()
            weatherPusherMap[cityName] = weatherPusher
        }

        return weatherPusher
    }

    override fun fetchCurrentWeather(cityName: String): Single<Weather> = if (error) {
        Single.error(NetworkErrorException("Failed to fetch network error"))
    } else {
        Single.just(mockWeather(cityName))
    }

    override fun getCurrentWeatherListener(cityName: String): MutableLiveData<Weather> =
        getWeatherPusher(cityName)
}
