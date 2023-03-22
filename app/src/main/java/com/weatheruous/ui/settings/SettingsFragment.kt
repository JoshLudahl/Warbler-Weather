package com.weatheruous.ui.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.weatheruous.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    val viewModel: SettingsViewModel by viewModels()
}
