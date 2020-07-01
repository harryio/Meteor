package io.github.sainiharry.meteor.common.model

import io.github.sainiharry.meteor.common.UniqueId

data class Weather(
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

    var id: Long = 0

    override fun getUniqueId(): Long = timestamp
}