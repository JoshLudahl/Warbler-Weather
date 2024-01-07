package com.warbler.ui.forecast.viewpager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.warbler.R
import com.warbler.databinding.FragmentForecastViewpagerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastViewPagerFragment : Fragment(R.layout.fragment_forecast_viewpager) {
    private var _binding: FragmentForecastViewpagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForecastViewPagerViewModel by viewModels()

    private lateinit var adapter: ViewPagerAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForecastViewpagerBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewPager.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
