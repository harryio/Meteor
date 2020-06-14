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
    fun getCurrentWeatherListener(cityName: String): LiveData<WeatherModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecast(forecasts: List<ForecastModel>)

    @Query("SELECT * FROM ForecastModel WHERE ForecastModel.cityName = :cityName LIMIT 3")
    fun getForecastListener(cityName: String): LiveData<List<ForecastModel>>
}