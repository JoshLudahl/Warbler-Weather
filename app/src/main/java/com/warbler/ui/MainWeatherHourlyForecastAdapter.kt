package com.warbler.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.HourlyForecastItem
import com.warbler.databinding.HourlyForecastListItemBinding

class MainWeatherHourlyForecastAdapter :
    ListAdapter<HourlyForecastItem, MainWeatherHourlyForecastAdapter.ViewHolder>(
        HourlyWeatherDiffUtil,
    ) {
    private var list = emptyList<HourlyForecastItem>()

    class ViewHolder(private val itemBinding: HourlyForecastListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(hourlyListItem: HourlyForecastItem) {
            itemBinding.weather = hourlyListItem
            itemBinding.weatherIcon.setImageResource(hourlyListItem.icon as Int)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return HourlyForecastListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        ).let {
            ViewHolder(it)
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }

    fun setItems(list: List<HourlyForecastItem>) {
        this.list = list
        submitList(list)
    }
}

object HourlyWeatherDiffUtil : DiffUtil.ItemCallback<HourlyForecastItem>() {
    override fun areItemsTheSame(
        oldItem: HourlyForecastItem,
        newItem: HourlyForecastItem,
    ): Boolean {
        return oldItem.hour == newItem.hour
    }

    override fun areContentsTheSame(
        oldItem: HourlyForecastItem,
        newItem: HourlyForecastItem,
    ): Boolean {
        return oldItem == newItem
    }
}
