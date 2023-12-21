package com.warbler.ui

import android.os.Bundle
import android.text.format.DateFormat.format
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.warbler.R
import com.warbler.data.model.weather.Alert
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.fromDoubleToPercentage
import com.warbler.data.model.weather.Conversion.getTimeFromTimeStamp
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Forecast
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherDataSourceDto
import com.warbler.data.model.weather.WeatherDataSourceDto.buildHourlyRainMap
import com.warbler.data.model.weather.WeatherDetailItem
import com.warbler.data.model.weather.WeatherForecast
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.databinding.FragmentMainWeatherBinding
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.ClickListenerInterface
import com.warbler.utilities.Resource
import com.warbler.utilities.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainWeatherFragment : Fragment(R.layout.fragment_main_weather) {
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainWeatherViewModel by viewModels()
    private val adapter = MainAdapter(
        object : ClickListenerInterface<WeatherForecast> {
            override fun onClick(item: WeatherForecast) {
                handleOnForecastItemClicked(item)
            }

            override fun delete(item: WeatherForecast) {
                // Not used
            }
        }
    )

    private fun handleOnForecastItemClicked(item: WeatherForecast) {
        viewModel.weatherObject.value?.let {
            Log.i("Speed Unit", "Speed Unit is: ${viewModel.speedUnit.value}")
            val action =
                MainWeatherFragmentDirections.actionMainWeatherFragmentToForecastFragment(
                    Forecast(
                        daily = it.daily[item.index],
                        temperature = viewModel.temperatureUnit.value,
                        timeZoneOffset = it.timezoneOffset,
                        speed = viewModel.speedUnit.value
                    )
                )

            findNavController().navigate(action)
        } ?: toast("Error getting forecast.")
    }

    private val weatherDetailAdapter = MainWeatherDetailItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

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
                icon = R.drawable.ic_wi_sunrise,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = result.current.sunrise.toLong(),
                    offset = result.timezoneOffset.toLong()
                ) + " AM",
                label = R.string.sunrise
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunset,
                value = Conversion.getTimeFromTimeStamp(
                    timeStamp = result.current.sunset.toLong(),
                    offset = result.timezoneOffset.toLong()
                ) + " PM",
                label = R.string.sunset
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_thermometer,
                value = Conversion.fromKelvinToProvidedUnit(
                    result.current.feelsLike,
                    viewModel.temperatureUnit.value
                ).toInt().toDegrees,
                label = R.string.feels_like
            )
        )
//
//        list.add(
//            WeatherDetailItem(
//                icon = R.drawable.ic_compress,
//                value = getString(R.string.pressure, result.current.pressure.toString()),
//                label = R.string.pressure_text
//            )
//        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_raindrops,
                value = Conversion.fromKelvinToProvidedUnit(
                    value = result.current.dewPoint,
                    unit = viewModel.temperatureUnit.value
                ).toInt().toDegrees,
                label = R.string.dew_point
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_cloud,
                value = getString(R.string.cloudy, result.current.clouds.toString()),
                label = R.string.clouds
            )
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_umbrella,
                value = getString(
                    R.string.percentage,
                    "${result.daily[0].pop.fromDoubleToPercentage}"
                ),
                label = R.string.chance_of_rain
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
            viewModel.updateWeatherData()
        }

        lifecycleScope.launch {
            viewModel.locationState.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        viewModel.updateWeatherData()
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

        lifecycleScope.launch {
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

            uvIndexValue.text = result.current.uvi.toString()

            val humidityString = String.format(
                getString(R.string.percent),
                result.current.humidity.toString()
            )
            humidityTextValue.text = humidityString

            val data = listOf(
                1703119477 - 28800 to 2f,
                1703123077 - 28800 to 6f,
                1703126677 - 28800 to 4f
            )
                .associate { (dateString, yValue) ->
                    Instant.ofEpochSecond(dateString.toLong()) to yValue
                }

            // Get the hourly wind
            val hourlyWind = buildHourlyRainMap(result).asIterable().associate {
                    (dateString, yValue) ->
                Instant.ofEpochSecond(dateString) to yValue
            }

            // Associate the keys to the date mapping to float
            val xValuesToDates = data.keys
                .associateBy { it.epochSecond.toFloat() }

            xValuesToDates.forEach {
                Log.i("Log", "Item: Key ${it.key.toLong()}, Value: ${it.value}")
                Log.i("Log", "${result.timezoneOffset}")
            }

            // create a chart entry model of the data, mapping the  values
            val chartEntryModel = entryModelOf(
                xValuesToDates.keys.zip(data.values, ::entryOf)
            )

            val horizontalAxisValueFormatter =
                AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                    (xValuesToDates[value] ?: Instant.ofEpochSecond(value.toLong()))
                        .atZone(ZoneId.of("UTC")).hour.let { it - 12 }.toString()
                }

            (chartView.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                .valueFormatter = horizontalAxisValueFormatter

            val chartEntryModelProducer = ChartEntryModelProducer()

            chartEntryModelProducer.setEntries(chartEntryModel.entries)
            chartView.setModel(chartEntryModel)
        }
    }

    private fun setUpListeners() {
        binding.settingsIcon.setOnClickListener {
            findNavController().navigate(R.id.action_mainWeatherFragment_to_settingsFragment)
        }

        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_mainWeatherFragment_to_locationFragment)
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
                binding.detailsIcon.setImageResource(R.drawable.ic_round_expand_circle_down)
            } else {
                binding.additionalDetailsLayout.visibility = View.VISIBLE
                binding.detailsIcon.setImageResource(R.drawable.ic_expand_up)
            }
        }

        binding.weatherAlertIcon.setOnClickListener {
            viewModel.weatherObject.value?.alerts?.get(0)?.let {
                val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Alert: ${it.event}")
                    .setMessage(it.description)
                    .setNegativeButton(getText(R.string.close)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()

                dialog.show()
            }
        }

        binding.weatherAlertIcon.setOnClickListener {
            viewModel.weatherObject.value?.alerts?.let { alerts ->
                val message = buildAlertMessage(alerts)
                val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setTitle("Alert: Weather Alert")
                    .setMessage(message)
                    .setNegativeButton(getText(R.string.close)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .create()
                dialog.show()
            }
        }
    }

    private fun toast(message: String) = requireContext().showToast(message)

    private fun buildAlertMessage(alerts: List<Alert>): String {
        val message = StringBuilder()
        alerts.forEach { alert ->
            message.append(alert.event)
            message.append("\n")
            message.append(alert.description)
            message.append("\n\n")
        }
        return message.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
