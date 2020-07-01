package io.github.sainiharry.meteor.weatherrepository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.sainiharry.meteor.common.model.Weather

@Dao
internal interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherModel: WeatherModel)

    @Query("SELECT * FROM WeatherModel WHERE WeatherModel.cityName = :cityName")
    fun getCurrentWeatherListener(cityName: String): LiveData<WeatherModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecasts: List<ForecastModel>)

    @Query("SELECT *, date(timestampStr) AS datetime FROM ForecastModel WHERE ForecastModel.cityName = :cityName AND timestampStr > date('now') GROUP BY date(timestampStr) ORDER BY datetime ASC LIMIT 3")
    fun getForecastListener(cityName: String): LiveData<List<Weather>>
}