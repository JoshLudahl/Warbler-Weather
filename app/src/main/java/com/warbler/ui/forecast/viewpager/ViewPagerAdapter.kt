package com.warbler.ui.forecast.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.warbler.data.model.weather.Forecast
import com.warbler.databinding.FragmentForecastViewpagerBinding

class ViewPagerAdapter(
    val days: List<Forecast>,
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(
        private val itemBinding: FragmentForecastViewpagerBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(forecastItem: Forecast) {
            itemBinding.forecast = forecastItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewPagerViewHolder {
        return FragmentForecastViewpagerBinding.inflate(
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
}
