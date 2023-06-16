package com.warbler.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.warbler.R
import com.warbler.data.model.weather.Alert
import com.warbler.databinding.FragmentDialogWeatherAlertBinding

class WeatherAlertDialogFragment(
    private val alert: Alert
) : DialogFragment() {
    private var _binding: FragmentDialogWeatherAlertBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherAlertDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            _binding = DataBindingUtil.inflate(
                layoutInflater.cloneInContext(context),
                R.layout.fragment_dialog_weather_alert,
                null,
                false
            )

            binding.viewModel = viewModel

            binding.alertEvent.text = alert.event
            binding.alertBody.text = alert.description

            binding.closeIcon.setOnClickListener {
                dialog?.cancel()
            }

            builder.setView(binding.root)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null or empty.")
    }

    companion object {
        const val TAG = "WeatherAlertDialog"
    }
}
