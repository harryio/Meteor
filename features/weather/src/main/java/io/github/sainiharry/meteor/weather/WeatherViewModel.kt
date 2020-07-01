package io.github.sainiharry.meteor.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.weatherrepository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

internal class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private var userQuery: String? = null

    private val cityNameLiveData = MutableLiveData<String>()

    private val hasUserQuery
        get() = !userQuery.isNullOrEmpty() && !userQuery.isNullOrBlank()

    val weather: LiveData<Weather>

    val forecast: LiveData<List<Weather>>

    val isCurrentWeatherVisible: LiveData<Boolean>

    init {
        weather = Transformations.switchMap(
            cityNameLiveData,
            weatherRepository::getCurrentWeatherListener
        )

        forecast = Transformations.switchMap(
            cityNameLiveData,
            weatherRepository::getForecastListener
        )

        isCurrentWeatherVisible = Transformations.map(weather) {
            it != null
        }
    }

    private var location: Pair<Double, Double>? = null

    fun handleUserLocation(lat: Double, lng: Double) {
        location = lat to lng
        refresh()
    }

    fun refresh() {
        when {
            hasUserQuery -> {
                loadWeatherData(userQuery)
            }

            location != null -> {
                location?.let { loadWeatherData(it.first, it.second) }
            }

            else -> {
                _loading.value = Event(false)
            }
        }
    }

    fun handleUserQuery(userQuery: String?) {
        this@WeatherViewModel.userQuery = userQuery

        if (userQuery.isNullOrEmpty() || userQuery.isBlank() || userQuery == cityNameLiveData.value) {
            return
        }

        loadWeatherData(userQuery)
    }

    private fun loadWeatherData(lat: Double, lng: Double) = loadWeatherData {
        weatherRepository.fetchCurrentWeather(lat, lng)
    }

    private fun loadWeatherData(cityName: String?) = cityName?.let {
        loadWeatherData {
            weatherRepository.fetchCurrentWeather(cityName)
        }
    }

    private fun loadWeatherData(weatherFetcher: suspend () -> Weather) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                _loading.value = Event(true)
                val weather = weatherFetcher()
                weatherRepository.fetchForecast(weather.cityName)
                cityNameLiveData.value = weather.cityName
            } catch (e: Exception) {
                handleError(e, R.string.error_current_weather_fetch)
            } finally {
                _loading.value = Event(false)
            }
        }
    }
}