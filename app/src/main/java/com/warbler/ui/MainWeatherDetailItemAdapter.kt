package com.warbler.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.WeatherDetailItem
import com.warbler.databinding.GridLayoutItemBinding

class MainWeatherDetailItemAdapter :
    ListAdapter<WeatherDetailItem, MainWeatherDetailItemAdapter.ViewHolder>(
        MainWeatherDetailItemDiffUtil,
    ) {
    private var weatherDetailList = emptyList<WeatherDetailItem>()

    class ViewHolder(
        private val itemBinding: GridLayoutItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(weatherDetailItem: WeatherDetailItem) {
            itemBinding.detail = weatherDetailItem
            itemBinding.detailIcon.setImageResource(weatherDetailItem.icon)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        GridLayoutItemBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ).let {
                ViewHolder(it)
            }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val currentItem = weatherDetailList[position]
        holder.bind(currentItem)
    }

    fun setItems(list: List<WeatherDetailItem>) {
        weatherDetailList = list
        submitList(list)
    }
}

object MainWeatherDetailItemDiffUtil : DiffUtil.ItemCallback<WeatherDetailItem>() {
    override fun areItemsTheSame(
        oldItem: WeatherDetailItem,
        newItem: WeatherDetailItem,
    ): Boolean = oldItem.value == newItem.value

    override fun areContentsTheSame(
        oldItem: WeatherDetailItem,
        newItem: WeatherDetailItem,
    ): Boolean = oldItem == newItem
}
