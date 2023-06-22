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
import com.warbler.R
import com.warbler.databinding.FragmentForecastBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    private val args: ForecastFragmentArgs by navArgs()

    private val viewModel: ForecastViewModel by viewModels {
        ForecastViewModelFactory(
            daily = args.weatherForecast,
            speedUnits = args.speed,
            args.tempUnits
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forecast, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupListeners()
        setUi()
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
