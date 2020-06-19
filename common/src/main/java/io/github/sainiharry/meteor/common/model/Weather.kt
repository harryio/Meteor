package io.github.sainiharry.meteor.common.model

import io.github.sainiharry.meteor.common.UniqueId

data class Weather(
    val id: Long,
    val main: String,
    val icon: String,
    val cityId: Long,
    val cityName: String,
    val temp: Float,
    val maxTemp: Float,
    val minTemp: Float,
    val country: String,
    val timestamp: Long
) : UniqueId {
    override fun getUniqueId(): Long = id
}