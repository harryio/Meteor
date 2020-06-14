package io.github.sainiharry.meteor.weatherrepository.database

import androidx.room.Embedded
import androidx.room.Entity
import io.github.sainiharry.meteor.common.Weather

@Entity(primaryKeys = ["cityName", "isCurrent"])
internal data class WeatherModel(@Embedded val weather: Weather, val isCurrent: Boolean = false)

internal fun WeatherModel.flatten() = Weather(
    weather.id,
    weather.main,
    weather.icon,
    weather.cityId,
    weather.cityName,
    weather.temp,
    weather.minTemp,
    weather.maxTemp,
    weather.country
)