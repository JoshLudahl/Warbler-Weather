package com.weatheruous.ui.settings

import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.weatheruous.R
import com.weatheruous.databinding.FragmentSettingsBinding
import com.weatheruous.utilities.Constants
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
        binding.lifecycleOwner = viewLifecycleOwner
        setupListeners()
    }

    private fun setupListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.privacyPolicyIcon.setOnClickListener {
            handleOnClickLink(Constants.PRIVACY_POLICY_URL)
        }

        binding.aboutIcon.setOnClickListener {
            handleOnClickLink(Constants.ABOUT_URL)
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
}
