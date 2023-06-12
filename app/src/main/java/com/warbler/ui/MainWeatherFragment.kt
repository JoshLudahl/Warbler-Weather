package com.warbler.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.warbler.R
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherDataSourceDto
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObservers()
        setUpListeners()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun updateRecyclerView(weatherForecastList: List<WeatherForecast>) {
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

                            updateRecyclerView(list)
                            setUiElements(result.data, viewModel.temperatureUnit.value)

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
            requireContext().showToast("Humidity")
        }
        binding.uvIndexIcon.setOnClickListener {
            requireContext().showToast("UV Index")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
