package io.github.sainiharry.meteor.currentweatherrepository

import com.squareup.moshi.Json

data class CurrentWeatherResponse(
    @Json(name = "id") val cityId: Long,
    @Json(name = "name") val cityName: String,
    val weather: WeatherResponse,
    @Json(name = "main")
    val weatherInfo: WeatherInfoResponse
)

data class WeatherResponse(val id: Long, val main: String, val description: String, val icon: String)

data class WeatherInfoResponse(
    val temp: Float,
    val pressure: Long,
    val humidity: Long,
    @Json(name = "temp_min")
    val minTemp: Float,
    @Json(name = "temp_max")
    val maxTemp: Float
)