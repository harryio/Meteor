package io.github.sainiharry.meteor.weatherrepository.network

import com.squareup.moshi.Json
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.weatherrepository.database.ForecastModel

internal data class ForecastResponse(val list: List<ForecastWeather>)

internal data class ForecastWeather(
    @Json(name = "main") val weatherInfoResponse: WeatherInfoResponse,
    @Json(name = "weather") val weatherConditionResponse: WeatherConditionResponse,
    val city: ForecastWeatherLocation
)

internal data class ForecastWeatherLocation(val id: Long, val name: String, val country: String)

internal fun ForecastResponse.flatten(): List<Weather> = list.map(ForecastWeather::flatten)

internal fun List<Weather>.toForecastModelList() = map { ForecastModel(it) }

private fun ForecastWeather.flatten(): Weather = Weather(
    weatherConditionResponse.id,
    weatherConditionResponse.main,
    weatherConditionResponse.icon,
    city.id,
    city.name,
    weatherInfoResponse.temp,
    weatherInfoResponse.maxTemp,
    weatherInfoResponse.minTemp,
    city.country
)