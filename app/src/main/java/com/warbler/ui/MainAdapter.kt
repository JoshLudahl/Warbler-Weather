package com.warbler.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.WeatherForecast
import com.warbler.databinding.WeatherForecastListItemBinding
import com.warbler.utilities.ClickListenerInterface

class MainAdapter(
    private val clickListener: ClickListenerInterface<WeatherForecast>
) : ListAdapter<WeatherForecast, MainAdapter.ViewHolder>(MainDiffViewCallback) {

    private var weatherData = emptyList<WeatherForecast>()

    class ViewHolder(private val itemBinding: WeatherForecastListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            clickListener: ClickListenerInterface<WeatherForecast>,
            weatherForecast: WeatherForecast
        ) {
            itemBinding.weather = weatherForecast
            itemBinding.clickListener = clickListener
            itemBinding.executePendingBindings()
            itemBinding.weatherIcon.setImageResource(weatherForecast.icon as Int)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return WeatherForecastListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let {
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = weatherData[position]
        holder.bind(clickListener = clickListener, weatherForecast = currentItem)
    }

    fun setItems(list: List<WeatherForecast>) {
        weatherData = list
        submitList(list)
    }
}

object MainDiffViewCallback : DiffUtil.ItemCallback<WeatherForecast>() {
    override fun areItemsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        return oldItem.dayOfWeek == newItem.dayOfWeek
    }

    override fun areContentsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        return oldItem == newItem
    }
}
