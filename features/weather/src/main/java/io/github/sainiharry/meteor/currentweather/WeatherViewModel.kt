package io.github.sainiharry.meteor.currentweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.currentweatherrepository.WeatherRepository
import io.reactivex.Scheduler

internal class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel() {

    private var userQuery: String? = null

    private val cityNameLiveData = MutableLiveData<String>()

    private val hasUserQuery
        get() = !userQuery.isNullOrEmpty() && !userQuery.isNullOrBlank()

    val weather: LiveData<Weather>
        get() = Transformations.switchMap(
            cityNameLiveData,
            weatherRepository::getCurrentWeatherListener
        )

    fun refresh() {
        if (hasUserQuery) {
            loadWeatherData(userQuery)
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
                weatherRepository.fetchCurrentWeather(cityName!!)
                    .observeOn(observableScheduler)
                    .ignoreElement()
                    .doOnEvent { _loading.value = Event(false) }
                    .subscribe({
                        cityNameLiveData.value = cityName
                    }, getErrorHandler(R.string.error_current_weather_fetch))
            )
        }
    }
}