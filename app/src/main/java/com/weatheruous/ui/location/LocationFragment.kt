package com.weatheruous.ui.location

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
    private val locationDatabaseAdapter = LocationAdapter()
    private val locationNetworkAdapter = LocationNetworkAdapter()

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
                viewModel.locationSearchList.collect { locationList ->
                    when (locationList) {
                        is Resource.Success -> {
                            Log.i("LocationFragment", "locationList ${locationList.data}")
                            Log.i("LocationFragment", "size ${locationList.data.size}")
                            locationDatabaseAdapter.setItems(locationList.data)
                        }
                        is Resource.Error -> {
                            Log.i("LocationFragment", "locationList Error")
                            binding.locationRecyclerView.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            Log.i("LocationFragment", "locationList Loading")
                            binding.locationRecyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locationSearchList.collect { locationList ->
                    when (locationList) {
                        is Resource.Success -> {
                            locationNetworkAdapter.setItems(locationList.data)
                            binding.searchResultRecyclerView.visibility = View.VISIBLE
                            binding.locationRecyclerView.visibility = View.GONE
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

        binding.searchResultRecyclerView.apply {
            adapter = locationNetworkAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpListeners() {
        binding.backIcon.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
        }

        binding.addLocation.setOnClickListener {
            listOf(binding.searchBarLayoutContainer, binding.searchBarEditText)
                .forEach { it.visibility = View.VISIBLE }
        }

        binding.closeSearch.setOnClickListener {
            listOf(binding.searchBarLayoutContainer, binding.searchBarEditText)
                .forEach { it.visibility = View.GONE }
        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /* Do nothing */
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count >= 3) {
                    // TODO: Implement search
                    viewModel.searchForLocation(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                /* Do nothing */
            }
        })
    }
}
