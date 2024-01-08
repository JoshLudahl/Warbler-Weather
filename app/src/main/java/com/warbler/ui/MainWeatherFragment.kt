package com.warbler.ui

import android.graphics.Color
import android.os.Bundle
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
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.legend.HorizontalLegend
import com.patrykandpatrick.vico.core.legend.Legend
import com.patrykandpatrick.vico.core.legend.LegendItem
import com.patrykandpatrick.vico.core.model.CartesianChartModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.warbler.R
import com.warbler.data.model.weather.Alert
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.bottomAxisValueFormatter
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.fromDoubleToPercentage
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Forecast
import com.warbler.data.model.weather.HourlyForecastItem
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherDataSourceDto
import com.warbler.data.model.weather.WeatherDetailItem
import com.warbler.data.model.weather.WeatherForecast
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.databinding.FragmentMainWeatherBinding
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.ClickListenerInterface
import com.warbler.utilities.Constants
import com.warbler.utilities.Resource
import com.warbler.utilities.doesAnyListContainValues
import com.warbler.utilities.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainWeatherFragment : Fragment(R.layout.fragment_main_weather) {
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainWeatherViewModel by viewModels()
    private val adapter =
        MainAdapter(
            object : ClickListenerInterface<WeatherForecast> {
                override fun onClick(item: WeatherForecast) {
                    handleOnForecastItemClicked(item)
                }

                override fun delete(item: WeatherForecast) {
                    // Not used
                }
            },
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
                        speed = viewModel.speedUnit.value,
                    ),
                )

            findNavController().navigate(action)
        } ?: toast("Error getting forecast.")
    }

    private val weatherDetailAdapter = MainWeatherDetailItemAdapter()
    private val weatherHourlyForecastAdapter = MainWeatherHourlyForecastAdapter()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        setupObservers()
        setUpListeners()
        setUpWeatherRecyclerView()
        setupWeatherDetailRecyclerView()
        setupWeatherHourlyForecastRecyclerView()
    }

    private fun setupWeatherHourlyForecastRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.hourlyRecyclerView.adapter = weatherHourlyForecastAdapter
        binding.hourlyRecyclerView.layoutManager = layoutManager
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
                value =
                    Conversion.getTimeFromTimeStamp(
                        timeStamp = result.current.sunrise.toLong(),
                        offset = result.timezoneOffset.toLong(),
                    ) + " AM",
                label = R.string.sunrise,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunset,
                value =
                    Conversion.getTimeFromTimeStamp(
                        timeStamp = result.current.sunset.toLong(),
                        offset = result.timezoneOffset.toLong(),
                    ) + " PM",
                label = R.string.sunset,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_thermometer,
                value =
                    Conversion.fromKelvinToProvidedUnit(
                        result.current.feelsLike,
                        viewModel.temperatureUnit.value,
                    ).roundToInt().toDegrees,
                label = R.string.feels_like,
            ),
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
                value =
                    Conversion.fromKelvinToProvidedUnit(
                        value = result.current.dewPoint,
                        unit = viewModel.temperatureUnit.value,
                    ).toInt().toDegrees,
                label = R.string.dew_point,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_cloud,
                value = getString(R.string.cloudy, result.current.clouds.toString()),
                label = R.string.clouds,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_umbrella,
                value =
                    getString(
                        R.string.percentage,
                        "${result.daily[0].pop.fromDoubleToPercentage}",
                    ),
                label = R.string.chance_of_rain,
            ),
        )

        return list
    }

    private fun updateWeatherDetailRecyclerView(weatherDetailItemList: List<WeatherDetailItem>) {
        weatherDetailAdapter.setItems(weatherDetailItemList)
    }

    private fun updateWeatherRecyclerView(weatherForecastList: List<WeatherForecast>) {
        adapter.setItems(weatherForecastList)
    }

    private fun updateHourlyForecastRecyclerView(hourlyForecastItem: List<HourlyForecastItem>) {
        weatherHourlyForecastAdapter.setItems(hourlyForecastItem)
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
                        val list =
                            WeatherDataSourceDto.buildWeatherForecast(
                                result.data,
                                viewModel.temperatureUnit.value,
                            )

                        updateWeatherRecyclerView(list)
                        val weatherDetailList = buildWeatherDetailList(result.data)

                        updateWeatherDetailRecyclerView(weatherDetailList)
                        updateHourlyForecastRecyclerView(
                            WeatherDataSourceDto.buildHourlyForecastList(
                                result.data,
                                viewModel.temperatureUnit.value,
                            ),
                        )
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

    private fun setUiElements(
        result: WeatherDataSource,
        value: Temperature,
    ) {
        with(binding) {
            currentTemperature.text =
                Conversion.fromKelvinToProvidedUnit(
                    result.current.temp,
                    value,
                ).roundToInt().toDegrees

            val feelsLikeTemp =
                Conversion.fromKelvinToProvidedUnit(
                    result.current.feelsLike,
                    value,
                ).roundToInt().toDegrees

            feelsLike.text = getString(R.string.feels_like_temp, feelsLikeTemp)

            weatherDescription.text = result.current.weather[0].description.capitalizeEachFirst

            currentWeatherIcon.setImageResource(
                result.current.weather[0].icon.getIconForCondition,
            )

            uvIndexValue.text = result.current.uvi.toString()

            binding.noDataInclude.title.text = getString(R.string.precipitation)
            binding.noDataInclude.message.text = getString(R.string.no_rain_in_forecast)

            val humidityString =
                String.format(
                    getString(R.string.percent),
                    result.current.humidity.toString(),
                )
            humidityTextValue.text = humidityString

            setUpHourlyRainChart(result)
            setUpHourlyTemperatureChart(result, value)
            setupHourlyUviChart(result)
            setupHourlyWindChart(result, viewModel?.speedUnit?.value ?: Speed.MPS)
            setUpHourlyHumidityChart(result)
        }
    }

    private fun setUpHourlyHumidityChart(result: WeatherDataSource) {
        val data =
            result.hourly.map {
                it.humidity.toInt()
            }

        val model =
            CartesianChartModel(
                LineCartesianLayerModel
                    .build { series(data) },
            )

        with(binding.hourlyHumidityChartView) {
            (chart?.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                .valueFormatter = result.bottomAxisValueFormatter

            (chart?.startAxis as VerticalAxis<AxisPosition.Vertical.Start>)
                .itemPlacer = Constants.CHART_COLUMN_DEFAULT

            setModel(model)
            visibility = View.VISIBLE
        }
    }

    private fun setupHourlyWindChart(
        result: WeatherDataSource,
        value: Speed,
    ) {
        val windSpeedData =
            result.hourly.map {
                Conversion.formatSpeedUnitsWithUnits(it.windSpeed, value).toInt()
            }

        val windGustData =
            result.hourly.map {
                Conversion.formatSpeedUnitsWithUnits(it.windGust, value).toInt()
            }

        val model =
            CartesianChartModel(
                LineCartesianLayerModel
                    .build {
                        series(windSpeedData)
                        series(windGustData)
                    },
            )

        with(binding.hourlyWindChartView) {
            (chart?.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                .valueFormatter = result.bottomAxisValueFormatter

            (chart?.startAxis as VerticalAxis<AxisPosition.Vertical.Start>)
                .itemPlacer = Constants.CHART_COLUMN_DEFAULT

            chart?.legend =
                getLegendsForRain(
                    Color.rgb(20, 108, 148) to "Wind",
                    Color.rgb(182, 187, 196) to "Gust",
                )

            setModel(model)
            visibility = View.VISIBLE
        }
    }

    private fun setupHourlyUviChart(result: WeatherDataSource) {
        val data =
            result.hourly.map { it.uvi }

        val model =
            CartesianChartModel(
                LineCartesianLayerModel
                    .build { series(data) },
            )

        with(binding.hourlyUviChartView) {
            (chart?.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                .valueFormatter = result.bottomAxisValueFormatter

            (chart?.startAxis as VerticalAxis<AxisPosition.Vertical.Start>)
                .itemPlacer = Constants.CHART_COLUMN_DEFAULT

            setModel(model)
            visibility = View.VISIBLE
        }
    }

    private fun setUpHourlyTemperatureChart(
        result: WeatherDataSource,
        temperatureUnit: Temperature,
    ) {
        val data =
            result.hourly.map {
                Conversion.fromKelvinToProvidedUnit(
                    it.temp,
                    temperatureUnit,
                ).roundToInt()
            }

        val model =
            CartesianChartModel(
                LineCartesianLayerModel
                    .build { series(data) },
            )

        val min = data.min() - Constants.TEMP_RANGE
        val max = data.max() + Constants.TEMP_RANGE
        val valueOverrider =
            AxisValueOverrider.fixed<LineCartesianLayerModel>(
                minY = min.toFloat(),
                maxY = max.toFloat(),
            )

        with(binding.hourlyTemperatureChartView) {
            (chart?.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                .valueFormatter = result.bottomAxisValueFormatter

            (chart?.startAxis as VerticalAxis<AxisPosition.Vertical.Start>)
                .itemPlacer = Constants.CHART_LINE_DEFAULT

            (chart?.layers?.get(0) as LineCartesianLayer).axisValueOverrider =
                valueOverrider

            setModel(model)
            visibility = View.VISIBLE
        }
    }

    private fun setUpHourlyRainChart(result: WeatherDataSource) {
        val hourlyRainFall =
            result.hourly.map {
                Conversion.convertOrReturnAccumulationByUnit(
                    it.rain?.h ?: 0.0,
                    viewModel.accumulationUnit.value,
                )
            }

        val hourlySnowFall =
            result.hourly.map {
                Conversion.convertOrReturnAccumulationByUnit(
                    it.snow?.h ?: 0.0,
                    viewModel.accumulationUnit.value,
                )
            }

        val list = listOf(hourlyRainFall, hourlySnowFall)

        if (doesAnyListContainValues(list)) {
            binding.noDataInclude.root.visibility = View.GONE
            val model =
                CartesianChartModel(
                    ColumnCartesianLayerModel
                        .build {
                            series(hourlyRainFall)
                            series(hourlySnowFall)
                        },
                )

            with(binding.hourlyRainChartView) {
                (chart?.bottomAxis as HorizontalAxis<AxisPosition.Horizontal.Bottom>)
                    .valueFormatter = result.bottomAxisValueFormatter

                (chart?.startAxis as VerticalAxis<AxisPosition.Vertical.Start>)
                    .itemPlacer = Constants.CHART_COLUMN_DEFAULT

                chart?.legend =
                    getLegendsForRain(
                        Color.rgb(56, 161, 232) to "Rain",
                        Color.rgb(255, 255, 255) to "Snow",
                    )

                setModel(model)
                visibility = View.VISIBLE
            }
        }
    }

    private fun getLegendsForRain(vararg legendItem: Pair<Int, String>): Legend {
        // Build a text component
        val textComponent = TextComponent.Builder()
        textComponent.color = Color.rgb(255, 211, 105)
        val tcbuilder = textComponent.build()

        // Add the legend item to the list
        val legendItems: MutableList<LegendItem> = ArrayList()
        legendItem.forEach { legend ->
            legendItems.add(LegendItem(ShapeComponent(color = legend.first), tcbuilder, legend.second))
        }

        // Return the legend item
        return HorizontalLegend(
            legendItems,
            10f,
            10f,
            10f,
            10f,
            MutableDimensions(10f, 10f),
        )
    }

    private fun setUpListeners() {
        binding.currentLocationIcon.setOnClickListener {
            handleSafeClick(R.id.action_mainWeatherFragment_to_locationFragment)
        }

        binding.settingsIcon.setOnClickListener {
            handleSafeClick(R.id.action_mainWeatherFragment_to_settingsFragment)
        }

        binding.searchIcon.setOnClickListener {
            handleSafeClick(R.id.action_mainWeatherFragment_to_locationFragment)
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
                val dialog =
                    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
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
                val dialog =
                    MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
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

    private fun handleSafeClick(direction: Int) {
        if (!viewModel.isDisabled.value) findNavController().navigate(direction)
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
