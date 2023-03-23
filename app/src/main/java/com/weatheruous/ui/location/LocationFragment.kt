package com.weatheruous.ui.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.weatheruous.R
import com.weatheruous.databinding.FragmentLocationBinding

class LocationFragment : Fragment(R.layout.fragment_location) {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLocationBinding.bind(view)
        binding.viewModel = viewModel

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
        }
    }
}
