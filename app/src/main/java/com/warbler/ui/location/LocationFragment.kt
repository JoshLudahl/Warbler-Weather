package com.warbler.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.warbler.R
import com.warbler.data.model.location.LocationEntity
import com.warbler.databinding.FragmentLocationBinding
import com.warbler.utilities.ClickListener
import com.warbler.utilities.ClickListenerInterface
import com.warbler.utilities.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : Fragment(R.layout.fragment_location) {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocationViewModel by viewModels()
    private val locationDatabaseAdapter =
        LocationAdapter(
            object : ClickListenerInterface<LocationEntity> {
                override fun onClick(item: LocationEntity) {
                    setLocationFromDatabaseAsCurrent(item)
                }

                override fun delete(item: LocationEntity) {
                    deleteLocationFromDatabase(item)
                }
            },
        )

    private fun deleteLocationFromDatabase(item: LocationEntity) {
        lifecycleScope.launch {
            viewModel.deleteFromDatabase(item)
        }
    }

    private val locationNetworkAdapter =
        LocationNetworkAdapter(
            ClickListener { location -> saveLocationSearchResultToDatabase(location) },
        )

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                // Permission granted, proceed with location request
                lifecycleScope.launch {
                    viewModel.getCurrentLocationAndSave()
                }
            } else {
                Log.d("LocationFragment", "Location permission denied")
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
                                "locationList Success: ${locationDatabaseAdapter.itemCount}",
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentLocationSaved.collect { saved ->
                    if (saved) {
                        Log.d("LocationFragment", "Current location saved, navigating back")
                        findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingCurrentLocation.collect { isLoading ->
                    view?.findViewById<android.widget.ProgressBar>(R.id.current_location_progress)?.let { progressBar ->
                        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        Log.d("LocationFragment", "Current location loading state: $isLoading")
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
        binding.topAppBar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.current_location_icon -> {
                    getCurrentLocationAndSave()
                    true
                }
                R.id.add_location_icon -> {
                    listOf(binding.searchBarLayoutContainer, binding.searchBarEditText)
                        .forEach { it.visibility = View.VISIBLE }
                    binding.locationRecyclerView.visibility = View.GONE
                    binding.searchBarEditText.requestFocus()
                    toggleKeyboardVisibility(binding.searchBarEditText)
                    true
                }
                else -> false
            }
        }

//        binding.topAppBar.setOnClickListener {
//            listOf(binding.searchBarLayoutContainer, binding.searchBarEditText)
//                .forEach { it.visibility = View.VISIBLE }
//            binding.locationRecyclerView.visibility = View.GONE
//            binding.searchBarEditText.requestFocus()
//            toggleKeyboardVisibility(binding.searchBarEditText)
//        }

        binding.closeSearch.setOnClickListener {
            closeAndClearSearch()
            toggleKeyboardVisibility1(binding.searchBarEditText)
        }

        binding.searchBarEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                    // Do nothing
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    s?.let {
                        if (it.length >= 3) {
                            Log.d("LocationFragment", "Searching for string: $s")
                            viewModel.searchForLocation(s.toString())
                        }
                    } ?: {
                        Log.d("LocationFragment", "Not enough chars to perform search.")
                        clearLocationSearchList()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    // Do nothing
                }
            },
        )
    }

    private fun toggleKeyboardVisibility(view: View) {
        activity?.let {
            val imm = it.getSystemService(InputMethodManager::class.java)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun toggleKeyboardVisibility1(view: View) {
        activity?.let {
            val imm = it.getSystemService(InputMethodManager::class.java)
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun saveLocationSearchResultToDatabase(location: LocationEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveToDatabase(location)
        }
        closeAndClearSearch()
        findNavController().navigate(R.id.action_locationFragment_to_mainWeatherFragment)
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

    private fun getCurrentLocationAndSave() {
        // Check if permissions are already granted
        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            // Permission already granted, proceed with location request
            lifecycleScope.launch {
                viewModel.getCurrentLocationAndSave()
            }
        } else {
            // Request permissions
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
