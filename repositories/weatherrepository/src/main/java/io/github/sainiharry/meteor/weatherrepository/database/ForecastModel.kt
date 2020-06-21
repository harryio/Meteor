package io.github.sainiharry.meteor.weatherrepository.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room representation of Weather Forecast
 */
@Entity
internal data class ForecastModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val main: String,
    val icon: String,
    val cityId: Long,
    val cityName: String,
    val temp: Float,
    val maxTemp: Float,
    val minTemp: Float,
    val country: String,
    val timestamp: Long
)