package com.warbler.ui.settings

import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.warbler.R
import com.warbler.databinding.FragmentSettingsBinding
import com.warbler.utilities.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupListeners()
        setupActionBar()
    }

    private fun setupActionBar() {
        binding.actionBarInclude.actionBarTitle.text = getString(R.string.settings)
    }

    private fun setupListeners() {
        binding.actionBarInclude.backArrowIcon.setOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.privacyPolicyIcon.setOnClickListener {
            handleOnClickLink(Constants.PRIVACY_POLICY_URL)
        }

        binding.aboutIcon.setOnClickListener {
            handleOnClickLink(Constants.ABOUT_URL)
        }

        binding.settingsSubheadingAboutThisAppText.setOnClickListener {
            handleOnClickLink(Constants.ABOUT_URL)
        }

        binding.settingsSubheadingPrivacyPolicyText.setOnClickListener {
            handleOnClickLink(Constants.PRIVACY_POLICY_URL)
        }

        binding.yellowRumpedWarblerIcon.setOnClickListener {
            handleOnClickLink(Constants.WARBLER_AUDUBON)
        }

        binding.yellowRumpedWarblerText.setOnClickListener {
            handleOnClickLink(Constants.WARBLER_AUDUBON)
        }

        binding.settingsSubheadingOptionCelsiusText.setOnClickListener {
            updateTemperatureRadioGroup(R.id.radio_celsius)
        }

        binding.settingsSubheadingOptionFahrenheitText.setOnClickListener {
            updateTemperatureRadioGroup(R.id.radio_fahrenheit)
        }

        binding.settingsSubheadingOptionKelvinText.setOnClickListener {
            updateTemperatureRadioGroup(R.id.radio_kelvin)
        }

        binding.settingsSubheadingOptionMpsText.setOnClickListener {
            updateSpeedRadioGroup(R.id.radio_mps)
        }

        binding.settingsSubheadingOptionMphText.setOnClickListener {
            updateSpeedRadioGroup(R.id.radio_mph)
        }

        binding.settingsSubheadingOptionKphText.setOnClickListener {
            updateSpeedRadioGroup(R.id.radio_kph)
        }

        binding.settingsSubheadingOptionInphText.setOnClickListener {
            updateAccumulationRadioGroup(R.id.radio_inph)
        }

        binding.settingsSubheadingOptionMmphText.setOnClickListener {
            updateAccumulationRadioGroup(R.id.radio_mmph)
        }
    }

    private fun updateTemperatureRadioGroup(itemId: Int) {
        when (itemId) {
            R.id.radio_celsius -> binding.radioCelsius
            R.id.radio_fahrenheit -> binding.radioFahrenheit
            R.id.radio_kelvin -> binding.radioKelvin
            else -> throw Exception()
        }.let {
            it.isChecked = true
            viewModel.handleTemperatureRadioClick(it.id)
        }
    }

    private fun updateSpeedRadioGroup(itemId: Int) {
        when (itemId) {
            R.id.radio_mps -> binding.radioMps
            R.id.radio_mph -> binding.radioMph
            R.id.radio_kph -> binding.radioKph
            else -> throw Exception("Invalid speed selection.")
        }.let {
            it.isChecked = true
            viewModel.handleSpeedRadioClick(it.id)
        }
    }

    private fun updateAccumulationRadioGroup(itemId: Int) {
        when (itemId) {
            R.id.radio_mmph -> binding.radioMmph
            R.id.radio_inph -> binding.radioInph
            else -> throw Exception("Invalid accumulation selection.")
        }.let {
            it.isChecked = true
            viewModel.handleAccumulationClick(it.id)
        }
    }

    private fun handleOnClickLink(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(requireContext(), url.toUri())
        } catch (exception: Exception) {
            Snackbar
                .make(binding.root, "Error opening link", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
