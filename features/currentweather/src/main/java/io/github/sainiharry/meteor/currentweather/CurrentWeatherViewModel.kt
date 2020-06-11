package io.github.sainiharry.meteor.currentweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel

class CurrentWeatherViewModel : BaseViewModel() {

    val weather: LiveData<Weather>
        get() = _weather

    private val _weather = MutableLiveData<Weather>()
}