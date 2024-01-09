package com.warbler.ui.forecast.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Forecast
import com.warbler.data.model.weather.WeatherDataSourceDto
import com.warbler.databinding.ForecastViewPagerItemBinding
import com.warbler.ui.MainWeatherDetailItemAdapter
import kotlin.math.roundToInt

class ViewPagerAdapter(
    private val days: List<Forecast>,
    private val position: Int,
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(
        private val itemBinding: ForecastViewPagerItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(forecastItem: Forecast) {
            itemBinding.forecast = forecastItem
            itemBinding.hiLow.temperatureLowText.text =
                Conversion.fromKelvinToProvidedUnit(
                    forecastItem.daily.temp.min,
                    forecastItem.temperature,
                ).roundToInt().toDegrees

            itemBinding.hiLow.temperatureHiText.text =
                Conversion.fromKelvinToProvidedUnit(
                    forecastItem.daily.temp.max,
                    forecastItem.temperature,
                ).roundToInt().toDegrees

            val weatherDetailList =
                WeatherDataSourceDto.buildWeatherDetailList(
                    itemBinding.root.context,
                    forecastItem,
                )

            val weatherDetailItemAdapter = MainWeatherDetailItemAdapter()
            val layoutManager = GridLayoutManager(itemBinding.root.context, 3)
            itemBinding.weatherDetailRecyclerView.adapter = weatherDetailItemAdapter
            itemBinding.weatherDetailRecyclerView.layoutManager = layoutManager
            weatherDetailItemAdapter.setItems(weatherDetailList)
            itemBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagerViewHolder {
        return ForecastViewPagerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        ).let {
            ViewPagerViewHolder(it)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(
        holder: ViewPagerViewHolder,
        position: Int,
    ) {
        val currentDay = days[position]
        holder.bind(currentDay)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.scrollToPosition(position)
    }
}
