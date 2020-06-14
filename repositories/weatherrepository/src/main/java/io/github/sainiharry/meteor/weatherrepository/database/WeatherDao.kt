package io.github.sainiharry.meteor.weatherrepository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weatherModel: WeatherModel)

    @Query("SELECT * FROM WeatherModel WHERE WeatherModel.cityName = :cityName")
    fun getWeatherListener(cityName: String): LiveData<WeatherModel>
}