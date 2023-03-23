package com.weatheruous.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.weatheruous.R
import com.weatheruous.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        binding.viewModel = viewModel

        setupListeners()
    }

    private fun setupListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_settingsFragment_to_mainWeatherFragment)
        }
    }
}
