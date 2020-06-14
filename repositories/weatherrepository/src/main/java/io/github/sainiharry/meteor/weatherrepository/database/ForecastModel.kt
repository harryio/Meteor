package io.github.sainiharry.meteor.weatherrepository.database

import androidx.room.Embedded
import androidx.room.Entity
import io.github.sainiharry.meteor.common.Weather

@Entity(primaryKeys = ["id"])
internal data class ForecastModel(@Embedded val weather: Weather)

internal fun ForecastModel.flatten() = Weather(
    weather.id,
    weather.main,
    weather.icon,
    weather.cityId,
    weather.cityName,
    weather.temp,
    weather.minTemp,
    weather.maxTemp,
    weather.country,
    weather.timestamp
)