package io.github.sainiharry.meteor.currentweatherrepository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
internal interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(currentWeatherModel: CurrentWeatherModel)

    @Query("SELECT * FROM CurrentWeatherModel WHERE CurrentWeatherModel.cityName = :cityName")
    fun getCurrentWeather(cityName: String): Flowable<CurrentWeatherModel>
}