package io.github.sainiharry.meteor.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.weatherrepository.WeatherRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @Mock
    lateinit var weatherRepository: WeatherRepository

    @Mock
    lateinit var weatherObserver: Observer<Weather>

    @Mock
    lateinit var forecastObserver: Observer<List<Weather>>

    @Mock
    lateinit var loadingObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var errorObserver: Observer<Event<Int>>

    @Mock
    lateinit var weatherViewVisibilityObserver: Observer<Boolean>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var model: WeatherViewModel

    private val weatherLiveData = MutableLiveData<Weather>()

    private val forecastLiveData = MutableLiveData<List<Weather>>()

    private val cityName = "Montreal"

    private val location = 34.23 to 383.32

    @Before
    fun setup() {
        model = WeatherViewModel(weatherRepository, testCoroutineDispatcher)
        model.weather.observeForever(weatherObserver)
        model.forecast.observeForever(forecastObserver)
        model.loading.observeForever(loadingObserver)
        model.error.observeForever(errorObserver)
        model.isCurrentWeatherVisible.observeForever(weatherViewVisibilityObserver)

        val mockWeather = mockWeather()
        runBlocking {
            `when`(weatherRepository.fetchCurrentWeather(cityName)).thenReturn(mockWeather)
            `when`(
                weatherRepository.fetchCurrentWeather(
                    location.first,
                    location.second
                )
            ).thenReturn(mockWeather)
            `when`(weatherRepository.fetchForecast(cityName)).thenReturn(listOf(mockWeather))
            `when`(weatherRepository.getCurrentWeatherListener(cityName)).thenReturn(weatherLiveData)
            `when`(weatherRepository.getForecastListener(cityName)).thenReturn(forecastLiveData)
        }
    }

    @After
    fun tearDown() {
        model.weather.removeObserver(weatherObserver)
        model.forecast.removeObserver(forecastObserver)
        model.loading.removeObserver(loadingObserver)
        model.error.removeObserver(errorObserver)
        model.isCurrentWeatherVisible.removeObserver(weatherViewVisibilityObserver)
    }

    @Test
    fun testInvalidCityName() {
        model.handleUserQuery(null)
        model.handleUserQuery("")
        model.handleUserQuery("          ")

        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(loadingObserver)
        verifyZeroInteractions(errorObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)
    }

    @Test
    fun testWeatherEvents() = runBlockingTest {
        model.handleUserQuery(cityName)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)
        verify(weatherViewVisibilityObserver).onChanged(true)

        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        verify(weatherObserver).onChanged(weather2)
        verify(weatherViewVisibilityObserver, times(2)).onChanged(true)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testThatOnlyLatestWeatherWillBeShown() = runBlockingTest {
        model.handleUserQuery(cityName)
        weatherLiveData.value = mockWeather()
        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        assertEquals(weather2, model.weather.value)
    }

    @Test
    fun testCityChange() = runBlockingTest {
        model.handleUserQuery(cityName)
        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)

        val newCity = "London"
        val newCityWeatherLiveData = MutableLiveData<Weather>()
        val mockWeather = mockWeather(
            newCity
        )
        `when`(weatherRepository.fetchCurrentWeather(newCity)).thenReturn(mockWeather)
        `when`(weatherRepository.getCurrentWeatherListener(newCity)).thenReturn(
            newCityWeatherLiveData
        )
        `when`(weatherRepository.fetchForecast(newCity)).thenReturn(listOf(mockWeather))

        model.handleUserQuery(newCity)
        verify(loadingObserver, times(2)).onChanged(Event(true))
        verify(loadingObserver, times(2)).onChanged(Event(false))

        weatherLiveData.value = mockWeather(temp = 383.1f)

        newCityWeatherLiveData.value = mockWeather
        verify(weatherObserver).onChanged(mockWeather)

        weatherLiveData.value = mockWeather(temp = 383.1f, maxTemp = 382.2f)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testWeatherResultsWithCoordinates() = runBlockingTest {
        model.handleUserLocation(location.first, location.second)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)
        verify(weatherViewVisibilityObserver).onChanged(true)

        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        verify(weatherObserver).onChanged(weather2)
        verify(weatherViewVisibilityObserver, times(2)).onChanged(true)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testRefresh() = runBlockingTest {
        model.refresh()
        verify(loadingObserver).onChanged(Event(false))
        verifyNoMoreInteractions(loadingObserver)

        model.handleUserLocation(location.first, location.second)
        verify(weatherRepository).fetchCurrentWeather(location.first, location.second)
        verify(weatherRepository).fetchForecast(cityName)

        model.refresh()
        verify(weatherRepository, times(2)).fetchCurrentWeather(location.first, location.second)
        verify(weatherRepository, times(2)).fetchForecast(cityName)

        model.handleUserQuery(cityName)
        verify(weatherRepository, never()).fetchCurrentWeather(cityName)

        val cityName = "New York"
        val mockWeather = mockWeather(cityName = cityName)
        `when`(weatherRepository.fetchCurrentWeather(cityName)).thenReturn(mockWeather)
        `when`(weatherRepository.fetchForecast(cityName)).thenReturn(listOf(mockWeather))
        model.handleUserQuery(cityName)
        verify(weatherRepository).fetchCurrentWeather(cityName)
        verify(weatherRepository).fetchForecast(cityName)

        model.refresh()
        verify(weatherRepository, times(2)).fetchCurrentWeather(cityName)
        verify(weatherRepository, times(2)).fetchForecast(cityName)
    }

    @Test
    fun testForecast() {
        model.handleUserQuery(cityName)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)

        val weather1 = mockWeather()
        forecastLiveData.value = listOf(weather1)
        verify(forecastObserver).onChanged(forecastLiveData.value)

        val weather2 = mockWeather(temp = 383.4f)
        forecastLiveData.value = listOf(weather1, weather2)
        verify(forecastObserver).onChanged(forecastLiveData.value)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testHandleUserLocation() {
        model.handleUserLocation(location.first, location.second)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisibilityObserver)

        val weather = mockWeather()

        weatherLiveData.value = weather
        verify(weatherObserver).onChanged(weatherLiveData.value)

        forecastLiveData.value = listOf(weather)
        verify(forecastObserver).onChanged(forecastLiveData.value)

        verifyNoMoreInteractions(weatherObserver)
        verifyNoMoreInteractions(forecastObserver)
    }
}

fun mockWeather(
    cityName: String = "Montreal",
    id: Long = cityName.hashCode().toLong(),
    main: String = "Clear",
    icon: String = "0445",
    cityId: Long = cityName.hashCode().toLong(),
    temp: Float = 38.3f,
    maxTemp: Float = 30.3f,
    minTemp: Float = 43.1f,
    country: String = "US",
    timestamp: Long = System.currentTimeMillis()
) = Weather(
    id,
    main,
    icon,
    cityId,
    cityName,
    temp,
    maxTemp,
    minTemp,
    country,
    timestamp
)
