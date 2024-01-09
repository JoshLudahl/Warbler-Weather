package com.warbler.ui.forecast.viewpager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.warbler.R
import com.warbler.databinding.FragmentForecastViewpagerBinding
import com.warbler.ui.MainWeatherDetailItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastViewPagerFragment : Fragment(R.layout.fragment_forecast_viewpager) {
    private var _binding: FragmentForecastViewpagerBinding? = null
    private val binding get() = _binding!!
    private val args: ForecastViewPagerFragmentArgs by navArgs()
    private val viewModel: ForecastViewPagerViewModel by viewModels {
        ForecastViewPagerViewModelFactory(
            args.forecasts,
        )
    }

    private lateinit var adapter: ViewPagerAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForecastViewpagerBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val list = args.forecasts.forecasts.sortedBy { it.daily.dt }
        adapter = ViewPagerAdapter(list, args.position)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.forecastTabLayout, binding.viewPager) { tab, position ->
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
