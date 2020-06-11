package io.github.sainiharry.meteor.common

data class Weather(
    val id: Long,
    val main: String,
    val icon: String,
    val cityId: Long,
    val cityName: String,
    val temp: Float,
    val minTemp: Float,
    val maxTemp: Float,
    val country: String
)