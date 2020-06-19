package io.github.sainiharry.meteor.weatherrepository.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.sainiharry.meteor.common.model.Weather

@JsonClass(generateAdapter = true)
internal data class WeatherResponse(
    @Json(name = "id") val cityId: Long,
    @Json(name = "name") val cityName: String,
    val weather: List<WeatherConditionResponse>,
    @Json(name = "main")
    val weatherInfo: WeatherInfoResponse,
    @Json(name = "sys")
    val weatherSys: WeatherSysResponse,
    val dt: Long
)

@JsonClass(generateAdapter = true)
internal data class WeatherConditionResponse(val id: Long, val main: String, val icon: String)

@JsonClass(generateAdapter = true)
internal data class WeatherInfoResponse(
    val temp: Float,
    @Json(name = "temp_min")
    val minTemp: Float,
    @Json(name = "temp_max")
    val maxTemp: Float
)

@JsonClass(generateAdapter = true)
internal data class WeatherSysResponse(
    val country: String
)

internal fun WeatherResponse.toWeather(): Weather =
    Weather(
        weather[0].id,
        weather[0].main,
        weather[0].icon,
        cityId,
        cityName,
        weatherInfo.temp,
        weatherInfo.minTemp,
        weatherInfo.maxTemp,
        weatherSys.country,
        dt
    )