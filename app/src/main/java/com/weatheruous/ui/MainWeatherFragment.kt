package com.weatheruous.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.weatheruous.R
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.model.weather.Conversion.capitalizeEachFirst
import com.weatheruous.data.model.weather.Conversion.toDegrees
import com.weatheruous.data.model.weather.Conversion.toFahrenheitFromKelvin
import com.weatheruous.data.model.weather.WeatherDataSource
import com.weatheruous.data.model.weather.WeatherIconSelection.getIconForCondition
import com.weatheruous.databinding.FragmentMainWeatherBinding
import com.weatheruous.utilities.Resource
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
        setUpListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch(Dispatchers.Main + job) {
            viewModel.locationState.collect { result ->
                when (result) {
                    is Resource.Success<*> -> {
                        viewModel.updateWeatherData()
                        binding.locationText.text = (result.data as LocationEntity).name
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
                    binding.progressBar.visibility = View.VISIBLE
                    when (result) {
                        is Resource.Success -> {
                            with(binding) {
                                currentTemperature.text = (result.data as WeatherDataSource)
                                    .current
                                    .temp
                                    .toFahrenheitFromKelvin
                                    .roundToInt()
                                    .toDegrees

                                weatherDescription.text = result.data
                                    .current
                                    .weather[0]
                                    .description
                                    .capitalizeEachFirst

                                currentWeatherIcon
                                    .setImageResource(
                                        result.data
                                            .current
                                            .weather[0]
                                            .icon
                                            .getIconForCondition
                                    )
                            }

                            binding.progressBar.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setUpListeners() {
        binding.settingsIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_settingsFragment)
        }

        binding.searchIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_locationFragment)
        }

        binding.textView.setOnClickListener {
            viewModel.updateWeatherData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
