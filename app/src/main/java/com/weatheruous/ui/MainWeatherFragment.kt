package com.weatheruous.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.weatheruous.R
import com.weatheruous.databinding.FragmentMainWeatherBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainWeatherFragment : Fragment(R.layout.fragment_main_weather) {
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainWeatherViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainWeatherBinding.bind(view)
        binding.viewModel = viewModel

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.settingsIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_settingsFragment)
        }

        binding.searchIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_mainWeatherFragment_to_locationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
