package com.warbler.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        ForecastViewModelFactory(args.weatherForecast)
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
    }
}
