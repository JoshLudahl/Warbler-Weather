package com.weatheruous.ui.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatheruous.R
import com.weatheruous.databinding.FragmentLocationBinding
import com.weatheruous.utilities.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : Fragment(R.layout.fragment_location) {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private val adapter = LocationAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLocationBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpListeners()
        setUpObservers()
        setUpRecyclerView()
    }

    private fun setUpObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locationList.collect { locationList ->
                    when (locationList) {
                        is Resource.Success -> {
                            adapter.setItems(locationList.data)
                        }
                        is Resource.Error -> {
                            binding.locationRecyclerView.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            binding.locationRecyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.locationRecyclerView.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
        }

        binding.addLocation.setOnClickListener { view ->
            binding.searchBar.visibility = View.VISIBLE
        }
    }
}
