package io.github.sainiharry.meteor.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.weatherrepository.WeatherRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable

internal class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val observableScheduler: Scheduler
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

    var location: Pair<Double, Double>? = null

    fun handleUserLocation(lat: Double, lng: Double) {
        location = lat to lng
        _loading.value = Event(true)
        disposables.add(
            weatherRepository.fetchCurrentWeather(lat, lng).handleWeatherResponse()
        )
    }

    fun refresh() {
        when {
            hasUserQuery -> {
                loadWeatherData(userQuery)
            }

            location != null -> {
                location?.let { handleUserLocation(it.first, it.second) }
            }

            else -> {
                _loading.value = Event(false)
            }
        }
    }

    fun handleUserQuery(userQuery: String?) {
        this@WeatherViewModel.userQuery = userQuery
        loadWeatherData(userQuery)
    }

    private fun loadWeatherData(cityName: String?) {
        if (hasUserQuery) {
            _loading.value = Event(true)
            disposables.add(
                weatherRepository.fetchCurrentWeather(cityName!!).handleWeatherResponse()
            )
        }
    }

    private fun Single<Weather>.handleWeatherResponse(): Disposable = map(
        Weather::cityName)
        .flatMap { cityName ->
            weatherRepository.fetchForecast(cityName)
                .map {
                    cityName
                }
        }
        .observeOn(observableScheduler)
        .doOnEvent { _, _ ->
            _loading.value = Event(false)
        }
        .subscribe({
            cityNameLiveData.value = it
        }, getErrorHandler(R.string.error_current_weather_fetch))
}