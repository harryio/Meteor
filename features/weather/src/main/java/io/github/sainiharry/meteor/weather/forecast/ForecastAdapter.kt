package io.github.sainiharry.meteor.weather.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import io.github.sainiharry.meteor.common.Weather
import io.github.sainiharry.meteor.commonfeature.MeteorDiffUtil
import io.github.sainiharry.meteor.weather.databinding.ItemForecastBinding

internal class ForecastAdapter :
    ListAdapter<Weather, ForecastViewHolder>(MeteorDiffUtil<Weather>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder =
        ForecastViewHolder(
            ItemForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) =
        holder.bind(getItem(position))
}