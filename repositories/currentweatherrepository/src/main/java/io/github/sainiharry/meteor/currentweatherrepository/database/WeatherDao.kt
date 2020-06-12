package io.github.sainiharry.meteor.currentweatherrepository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeather(currentWeatherModel: CurrentWeatherModel)

    @Query("SELECT * FROM CurrentWeatherModel WHERE CurrentWeatherModel.cityName = :cityName")
    fun getCurrentWeatherListener(cityName: String): LiveData<CurrentWeatherModel>
}