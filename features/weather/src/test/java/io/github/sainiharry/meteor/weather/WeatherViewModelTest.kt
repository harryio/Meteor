package io.github.sainiharry.meteor.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.weatherrepository.WeatherRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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
    lateinit var loadingObserver: Observer<Event<Boolean>>

    @Mock
    lateinit var errorObserver: Observer<Event<Int>>

    @Mock
    lateinit var weatherViewVisiblityObserver: Observer<Boolean>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherViewModel: WeatherViewModel

    private val weatherLiveData = MutableLiveData<Weather>()

    private val cityName = "Montreal"

    private val location = 34.23 to 383.32

    @Before
    fun setup() {
        weatherViewModel = WeatherViewModel(weatherRepository, Schedulers.trampoline())
        weatherViewModel.weather.observeForever(weatherObserver)
        weatherViewModel.loading.observeForever(loadingObserver)
        weatherViewModel.error.observeForever(errorObserver)
        weatherViewModel.isCurrentWeatherVisible.observeForever(weatherViewVisiblityObserver)

        `when`(weatherRepository.fetchCurrentWeather(cityName)).thenReturn(Single.just(mockWeather()))
        `when`(weatherRepository.fetchCurrentWeather(cityName)).thenReturn(Single.just(mockWeather()))
        `when`(weatherRepository.fetchCurrentWeather(location.first, location.second)).thenReturn(
            Single.just(mockWeather())
        )
        `when`(weatherRepository.getCurrentWeatherListener(cityName)).thenReturn(weatherLiveData)
        `when`(weatherRepository.getCurrentWeatherListener(cityName)).thenReturn(weatherLiveData)
    }

    @After
    fun tearDown() {
        weatherViewModel.weather.removeObserver(weatherObserver)
        weatherViewModel.loading.removeObserver(loadingObserver)
        weatherViewModel.error.removeObserver(errorObserver)
        weatherViewModel.isCurrentWeatherVisible.removeObserver(weatherViewVisiblityObserver)
    }

    @Test
    fun testInvalidCityName() {
        weatherViewModel.handleUserQuery(null)
        weatherViewModel.handleUserQuery("")
        weatherViewModel.handleUserQuery("          ")

        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(loadingObserver)
        verifyZeroInteractions(errorObserver)
        verifyZeroInteractions(weatherViewVisiblityObserver)
    }

    @Test
    fun testWeatherEvents() {
        weatherViewModel.handleUserQuery(cityName)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisiblityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)
        verify(weatherViewVisiblityObserver).onChanged(true)

        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        verify(weatherObserver).onChanged(weather2)
        verify(weatherViewVisiblityObserver, times(2)).onChanged(true)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testThatOnlyLatestWeatherWillBeShown() {
        weatherViewModel.handleUserQuery(cityName)
        weatherLiveData.value = mockWeather()
        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        assertEquals(weather2, weatherViewModel.weather.value)
    }

    @Test
    fun testCityChange() {
        weatherViewModel.handleUserQuery(cityName)
        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisiblityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)

        val newCity = "London"
        val newCityWeatherLiveData = MutableLiveData<Weather>()
        `when`(weatherRepository.fetchCurrentWeather(newCity)).thenReturn(
            Single.just(
                mockWeather(
                    newCity
                )
            )
        )
        `when`(weatherRepository.getCurrentWeatherListener(newCity)).thenReturn(
            newCityWeatherLiveData
        )

        weatherViewModel.handleUserQuery(newCity)
        verify(loadingObserver, times(2)).onChanged(Event(true))
        verify(loadingObserver, times(2)).onChanged(Event(false))

        weatherLiveData.value = mockWeather(temp = 383.1f)

        val weather2 = mockWeather(newCity)
        newCityWeatherLiveData.value = weather2
        verify(weatherObserver).onChanged(weather2)

        weatherLiveData.value = mockWeather(temp = 383.1f, maxTemp = 382.2f)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testWeatherResultsWithCoordinates() {
        weatherViewModel.handleUserLocation(location.first, location.second)

        verify(loadingObserver).onChanged(Event(true))
        verify(loadingObserver).onChanged(Event(false))
        verifyZeroInteractions(weatherObserver)
        verifyZeroInteractions(weatherViewVisiblityObserver)

        val weather1 = mockWeather()
        weatherLiveData.value = weather1
        verify(weatherObserver).onChanged(weather1)
        verify(weatherViewVisiblityObserver).onChanged(true)

        val weather2 = mockWeather(temp = 383.4f)
        weatherLiveData.value = weather2
        verify(weatherObserver).onChanged(weather2)
        verify(weatherViewVisiblityObserver, times(2)).onChanged(true)

        verifyNoMoreInteractions(loadingObserver)
        verifyNoMoreInteractions(weatherObserver)
        verifyZeroInteractions(errorObserver)
    }

    @Test
    fun testRefresh() {
        weatherViewModel.refresh()
        verify(loadingObserver).onChanged(Event(false))
        verifyNoMoreInteractions(loadingObserver)

        weatherViewModel.handleUserLocation(location.first, location.second)
        verify(weatherRepository).fetchCurrentWeather(location.first, location.second)

        weatherViewModel.refresh()
        verify(weatherRepository, times(2)).fetchCurrentWeather(location.first, location.second)

        weatherViewModel.handleUserQuery(cityName)
        verify(weatherRepository).fetchCurrentWeather(cityName)

        weatherViewModel.refresh()
        verify(weatherRepository, times(2)).fetchCurrentWeather(cityName)
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
    country: String = "US"
) = Weather(id, main, icon, cityId, cityName, temp, maxTemp, minTemp, country)
