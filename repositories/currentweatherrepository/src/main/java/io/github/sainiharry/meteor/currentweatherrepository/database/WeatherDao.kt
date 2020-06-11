package io.github.sainiharry.meteor.currentweatherrepository.database

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable

@Dao
internal interface WeatherDao {

    @Query("SELECT * FROM CurrentWeatherModel WHERE CurrentWeatherModel.cityName = :cityName")
    fun getCurrentWeather(cityName: String): Flowable<CurrentWeatherModel>
}