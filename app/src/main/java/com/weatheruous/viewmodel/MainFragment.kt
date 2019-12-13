package com.weatheruous.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.weatheruous.R
import com.weatheruous.data.WeatherDatabase
import com.weatheruous.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.activity_main.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

//    private val viewModel: WeatherViewModel by lazy {
//        ViewModelProviders.of(this).get(WeatherViewModel::class.java)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        val dataSource = WeatherDatabase.getInstance(application).weatherDatabaseDao
        val viewModelFactory = WeatherViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(WeatherViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.refreshWeather.setOnClickListener {
            viewModel.refreshData()
        }

        // Giving the binding access to the OverviewViewModel
        setHasOptionsMenu(true)
        return binding.root
    }
}
