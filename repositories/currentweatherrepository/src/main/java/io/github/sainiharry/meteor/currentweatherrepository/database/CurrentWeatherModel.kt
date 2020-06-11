package io.github.sainiharry.meteor.currentweatherrepository.database

import androidx.room.Embedded
import androidx.room.Entity
import io.github.sainiharry.meteor.common.Weather

@Entity(primaryKeys = ["cityName"])
internal data class CurrentWeatherModel(@Embedded val weather: Weather)