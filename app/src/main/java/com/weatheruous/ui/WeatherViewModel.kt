package com.weatheruous.ui
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weatheruous.data.WeatherAPI
import com.weatheruous.data.WeatherDatabaseDao
import com.weatheruous.data.WeatherUrls
import com.weatheruous.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DateFormat
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(): ViewModel() {

}