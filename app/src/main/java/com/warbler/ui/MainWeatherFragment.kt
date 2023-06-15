package com.warbler.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.warbler.R
import com.warbler.data.model.weather.Alert
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherDataSourceDto
import com.warbler.data.model.weather.WeatherDetailItem
import com.warbler.data.model.weather.WeatherForecast
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.databinding.FragmentMainWeatherBinding
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.Resource
import com.warbler.utilities.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainWeatherFragment : Fragment(R.layout.fragment_main_weather) {
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainWeatherViewModel by viewModels()
    private val job = Job()
    private val adapter = MainAdapter()
    private val weatherDetailAdapter = MainWeatherDetailItemAdapter()
    private var alert: Alert? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()
        setUpListeners()
        setUpWeatherRecyclerView()
        setupWeatherDetailRecyclerView()
    }

    private fun setUpWeatherRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun setupWeatherDetailRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.weatherDetailRecyclerView.adapter = weatherDetailAdapter
        binding.weatherDetailRecyclerView.layoutManager = layoutManager
    }

    private fun buildWeatherDetailList(result: WeatherDataSource): List<WeatherDetailItem> {
        val list = ArrayList<WeatherDetailItem>()

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_sunrise,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = result.current.sunrise.toLong(),
                    offset = result.timezoneOffset.toLong()
                ) + " AM"
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_sunset,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = result.current.sunset.toLong(),
                    offset = result.timezoneOffset.toLong()
                ) + " PM"
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_thermostat,
                value = Conversion.fromKelvinToProvidedUnit(
                    result.current.feelsLike,
                    viewModel.temperatureUnit.value
                ).toInt().toDegrees
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_compress,
                value = getString(R.string.pressure, result.current.pressure.toString())

            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_water,
                value = Conversion.fromKelvinToProvidedUnit(
                    value = result.current.dewPoint,
                    unit = viewModel.temperatureUnit.value
                ).toInt().toDegrees
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_cloud,
                value = getString(R.string.cloudy, result.current.clouds.toString())
            )
        )

        return list
    }

    private fun updateWeatherDetailRecyclerView(weatherDetailItemList: List<WeatherDetailItem>) {
        weatherDetailAdapter.setItems(weatherDetailItemList)
    }

    private fun updateWeatherRecyclerView(weatherForecastList: List<WeatherForecast>) {
        adapter.setItems(weatherForecastList)
    }

    private fun setupObservers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.updateWeatherData(requireContext())
        }

        lifecycleScope.launch(Dispatchers.Main + job) {
            viewModel.locationState.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        viewModel.updateWeatherData(requireContext())
                    }
                    is Resource.Error -> {
                        // TODO
                    }
                    is Resource.Loading -> {
                        // TODO
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main + job) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.weatherState.collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            val list = WeatherDataSourceDto.buildWeatherForecast(
                                result.data,
                                viewModel.temperatureUnit.value
                            )

                            updateWeatherRecyclerView(list)
                            val weatherDetailList = buildWeatherDetailList(result.data)
                            updateWeatherDetailRecyclerView(weatherDetailList)
                            setUiElements(result.data, viewModel.temperatureUnit.value)
                            checkForWeatherAlerts(result.data)

                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        is Resource.Error -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                            // TODO add error view
                        }
                        is Resource.Loading -> {
                            binding.swipeRefreshLayout.isRefreshing = true
                        }
                    }
                }
            }
        }
    }

    private fun setUiElements(result: WeatherDataSource, value: Temperature) {
        with(binding) {
            currentTemperature.text = Conversion.fromKelvinToProvidedUnit(
                result.current.temp,
                value
            ).roundToInt().toDegrees

            weatherDescription.text = result.current.weather[0].description.capitalizeEachFirst

            currentWeatherIcon.setImageResource(
                result.current.weather[0].icon.getIconForCondition
            )

            temperatureLowText.text =
                Conversion.fromKelvinToProvidedUnit(result.daily[0].temp.min, value)
                    .roundToInt().toDegrees

            temperatureHiText.text = Conversion.fromKelvinToProvidedUnit(
                result.daily[0].temp.max,
                value
            ).roundToInt().toDegrees

            uvIndexValue.text = result.current.uvi.toString()

            val humidityString = String.format(
                getString(R.string.percent),
                result.current.humidity.toString()
            )
            humidityTextValue.text = humidityString
        }
    }

    private fun setUpListeners() {
        binding.settingsIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_settingsFragment)
        }

        binding.searchIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_locationFragment)
        }

        binding.humidityIcon.setOnClickListener {
            toast("Humidity")
        }
        binding.uvIndexIcon.setOnClickListener {
            toast("UV Index")
        }

        binding.detailsIcon.setOnClickListener {
            val state = binding.additionalDetailsLayout.visibility
            if (state == View.VISIBLE) {
                binding.additionalDetailsLayout.visibility = View.GONE
                binding.detailsIcon.setImageResource(R.drawable.ic_expand_down)
            } else {
                binding.additionalDetailsLayout.visibility = View.VISIBLE
                binding.detailsIcon.setImageResource(R.drawable.ic_expand_up)
            }
        }

        binding.weatherAlertIcon.setOnClickListener {
            alert?.let {
                WeatherAlertDialogFragment(it).show(parentFragmentManager, "Weather Alert")
            }
        }
    }

    private fun checkForWeatherAlerts(weatherDataSource: WeatherDataSource) {
        weatherDataSource.alerts?.let {
            alert = it[0]
            binding.weatherAlertIcon.visibility = View.VISIBLE
        } ?: { alert = null }
    }
    private fun toast(message: String) = requireContext().showToast(message)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
