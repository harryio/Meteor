package io.github.sainiharry.meteor.weatherrepository.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.weatherrepository.database.ForecastModel

@JsonClass(generateAdapter = true)
internal data class ForecastResponse(
    val list: List<ForecastWeather>, val city: ForecastWeatherLocation
)

@JsonClass(generateAdapter = true)
internal data class ForecastWeather(
    @Json(name = "main") val weatherInfoResponse: WeatherInfoResponse,
    @Json(name = "weather") val weatherConditions: List<WeatherConditionResponse>,
    val dt: Long
)

@JsonClass(generateAdapter = true)
internal data class ForecastWeatherLocation(val id: Long, val name: String, val country: String)

internal fun ForecastResponse.toWeatherList(): List<Weather> = list.map {
    it.toWeather(city)
}

internal fun List<Weather>.toForecastModelList() = map { ForecastModel(it) }

private fun ForecastWeather.toWeather(forecastWeatherLocation: ForecastWeatherLocation): Weather =
    Weather(
        weatherConditions[0].id,
        weatherConditions[0].main,
        weatherConditions[0].icon,
        forecastWeatherLocation.id,
        forecastWeatherLocation.name,
        weatherInfoResponse.temp,
        weatherInfoResponse.maxTemp,
        weatherInfoResponse.minTemp,
        forecastWeatherLocation.country,
        dt
    )