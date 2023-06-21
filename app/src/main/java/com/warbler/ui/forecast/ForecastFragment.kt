package com.warbler.ui.forecast

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.navArgs
import com.warbler.R
import com.warbler.databinding.FragmentForecastBinding

class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!
    private val args: ForecastFragmentArgs by navArgs()
    private val viewModel: ForecastViewModel = ViewModelProvider


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForecastBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }
}
