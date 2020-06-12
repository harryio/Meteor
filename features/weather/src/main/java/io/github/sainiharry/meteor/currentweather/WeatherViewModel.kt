package io.github.sainiharry.meteor.currentweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.BaseViewModel
import io.github.sainiharry.meteor.commonfeature.Event
import io.github.sainiharry.meteor.currentweatherrepository.WeatherRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val observableScheduler: Scheduler
) : BaseViewModel() {

    private val cityNameObserver: Subject<String> = PublishSubject.create()

    val weather: LiveData<Weather>
        get() = _weather

    private val _weather = MutableLiveData<Weather>()

    init {
        val weatherObservableSubscription = cityNameObserver.toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .switchMap { weatherRepository.getCurrentWeatherListener(it) }
            .subscribe {
                _weather.value = it
            }
        disposables.add(weatherObservableSubscription)
    }

    fun loadWeatherData(cityName: String) {
        if (cityName.isEmpty() || cityName.isBlank()) {
            return
        }

        _loading.value = Event(true)
        // TODO: 11/06/20 Add error handling
        disposables.add(weatherRepository.fetchCurrentWeather(cityName)
            .observeOn(observableScheduler)
            .ignoreElement()
            .subscribe { cityNameObserver.onNext(cityName) }
        )
    }
}