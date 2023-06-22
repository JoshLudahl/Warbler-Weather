package com.warbler.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.warbler.R
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.formatSpeedUnitsWithUnits
import com.warbler.data.model.weather.Conversion.fromDoubleToPercentage
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Daily
import com.warbler.data.model.weather.WeatherDetailItem
import com.warbler.databinding.FragmentForecastBinding
import com.warbler.ui.MainWeatherDetailItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    private val args: ForecastFragmentArgs by navArgs()
    private val weatherDetailAdapter = MainWeatherDetailItemAdapter()

    private val viewModel: ForecastViewModel by viewModels {
        ForecastViewModelFactory(
            args.forecast
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forecast, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupListeners()
        setUi()
        setupWeatherDetailRecyclerView()
        updateAdapter(buildWeatherDetailList(viewModel.forecast.daily))
    }

    private fun buildWeatherDetailList(daily: Daily): List<WeatherDetailItem> {
        val list = ArrayList<WeatherDetailItem>()

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunrise,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = viewModel.forecast.daily.sunrise.toLong(),
                    offset = viewModel.forecast.timeZoneOffset.toLong()
                ) + " AM",
                label = R.string.sunrise
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunset,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = viewModel.forecast.daily.sunset.toLong(),
                    offset = viewModel.forecast.timeZoneOffset.toLong()
                ) + " PM",
                label = R.string.sunset
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_raindrops,
                value = Conversion.fromKelvinToProvidedUnit(
                    value = daily.dewPoint,
                    unit = viewModel.forecast.temperature
                ).toInt().toDegrees,
                label = R.string.dew_point
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_cloud,
                value = getString(R.string.cloudy, daily.clouds.toString()),
                label = R.string.clouds
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_umbrella,
                value = getString(
                    R.string.percentage,
                    "${daily.pop.fromDoubleToPercentage}"
                ),
                label = R.string.chance_of_rain
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_humidity,
                value = getString(
                    R.string.percentage,
                    "${daily.humidity}"
                ),
                label = R.string.humidity
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_day_sunny,
                value = daily.uvi.toString(),
                label = R.string.uv_index
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_barometer,
                value = daily.pressure.toString(),
                label = R.string.pressure_text
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_strong_wind,
                value = formatSpeedUnitsWithUnits(
                    value = daily.windSpeed ?: 0.00,
                    speed = viewModel.forecast.speed
                ),
                label = R.string.wind
            )
        )

        return list
    }

    private fun setupWeatherDetailRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.weatherDetailRecyclerView.adapter = weatherDetailAdapter
        binding.weatherDetailRecyclerView.layoutManager = layoutManager
    }

    private fun updateAdapter(weatherDetailItem: List<WeatherDetailItem>) {
        weatherDetailAdapter.setItems(weatherDetailItem)
    }

    private fun setUi() {
        binding.hiLow.temperatureHiText.text = viewModel.maxTemperature
        binding.hiLow.temperatureLowText.text = viewModel.minTemperature
    }

    private fun setupListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigateUp()
        }
    }
}
