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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.weatheruous.R
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.databinding.FragmentLocationBinding
import com.weatheruous.utilities.ClickListener
import com.weatheruous.utilities.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : Fragment(R.layout.fragment_location) {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private val locationDatabaseAdapter = LocationAdapter(
        ClickListener { location -> setLocationFromDatabaseAsCurrent(location) }
    )
    private val locationNetworkAdapter = LocationNetworkAdapter(
        ClickListener { location -> saveLocationSearchResultToDatabase(location) }
    )

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
                            Log.d("LocationFragment", "locationList ${locationList.data}")
                            Log.d("LocationFragment", "size ${locationList.data.size}")
                            locationDatabaseAdapter.setItems(locationList.data)

                            Log.d(
                                "LocationFragment",
                                "locationList Success: ${locationDatabaseAdapter.itemCount}"
                            )
                            binding.locationRecyclerView.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            Log.d("LocationFragment", "locationList Error")
                            // binding.locationRecyclerView.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            Log.d("LocationFragment", "locationList Loading")
                            // binding.locationRecyclerView.visibility = View.GONE
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
                            // binding.locationRecyclerView.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            // binding.locationRecyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.locationRecyclerView.apply {
            adapter = locationDatabaseAdapter
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
            closeAndClearSearch()
        }

        binding.searchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /* Do nothing */
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count >= 3) {
                    Log.d("LocationFragment", "Searching for string: $s")
                    viewModel.searchForLocation(s.toString())
                } else {
                    clearLocationSearchList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                /* Do nothing */
            }
        })
    }

    private fun saveLocationSearchResultToDatabase(location: LocationEntity) {
        lifecycleScope.launch {
            viewModel.saveToDatabase(location)
        }
        closeAndClearSearch()
    }

    private fun setLocationFromDatabaseAsCurrent(location: LocationEntity) {
        lifecycleScope.launch {
            viewModel.updateCurrentLocation(location)
        }
        findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
    }
    private fun closeAndClearSearch() {
        listOf(binding.searchBarLayoutContainer, binding.searchBarEditText)
            .forEach { it.visibility = View.GONE }
        binding.searchBarEditText.text?.clear()
        binding.locationRecyclerView.visibility = View.VISIBLE
        clearLocationSearchList()
    }
    private fun clearLocationSearchList() = locationNetworkAdapter.setItems(arrayListOf())

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
