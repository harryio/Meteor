package io.github.sainiharry.meteor.weather.forecast

import android.text.format.DateFormat
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import io.github.sainiharry.meteor.common.model.Weather
import io.github.sainiharry.meteor.weather.R
import io.github.sainiharry.meteor.weather.databinding.ItemForecastBinding

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

        binding.forecastWeatherTime.text =
            DateFormat.format(TIMESTAMP_FORMAT, weather.timestamp * 1000)
        binding.executePendingBindings()
    }
}