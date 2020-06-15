package io.github.sainiharry.meteor.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel

class WeatherInfoViewModel : BaseViewModel() {

    internal val resultsFetchedForLocation = MutableLiveData(false)

    val weather: LiveData<Weather>
        get() = _weather
    private val _weather = MutableLiveData<Weather>()

    internal fun handleWeatherInfo(weather: Weather) {
        _weather.value = weather
    }
}