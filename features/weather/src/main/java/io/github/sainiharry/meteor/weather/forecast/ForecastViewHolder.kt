package io.github.sainiharry.meteor.weather.forecast

import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.weather.R
import io.github.sainiharry.meteor.weather.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.*

private const val TIMESTAMP_FORMAT = "EEE, dd MMM"

internal class ForecastViewHolder(private val binding: ItemForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(weather: Weather) {
        binding.weather = weather
        binding.forecastWeatherIcon.load(
            binding.forecastWeatherIcon.context.getString(
                R.string.weather_icon_url,
                weather.icon
            )
        ) {
            crossfade(true)
        }

        val sdf = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault())
        binding.forecastWeatherTime.text = sdf.format(Date(weather.timestamp))
        binding.executePendingBindings()
    }
}