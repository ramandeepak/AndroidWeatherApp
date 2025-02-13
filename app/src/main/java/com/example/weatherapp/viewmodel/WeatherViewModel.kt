package com.example.weatherapp.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.HourWeather
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.utils.network.LocationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherViewModel(private val repository: WeatherRepository = WeatherRepositoryImpl()): ViewModel() {

    companion object {
        const val HTTPS_PREFIX = "https://"
        const val TIME_ONLY_FORMAT = "hh:mm a"
        const val DATE_AND_TIME_FORMAT = "MMMM dd, yyyy \n hh:mm a"
        const val PROGRESS_INDICATOR_DELAY: Long = 3000
    }

    private val _weatherForecast = MutableLiveData<WeatherResponse?>()
    val weatherForecast: LiveData<WeatherResponse?> = _weatherForecast

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val observedGeoCoordinates: LiveData<Pair<Double, Double>> = LocationManager.instance.geoCoordinates

    init {
        observedGeoCoordinates.observeForever { geoCoordinates ->
            fetchWeatherData("${geoCoordinates.first},${geoCoordinates.second}")
        }
    }

    fun getCityName(): String? = weatherForecast.value?.location?.name

    fun getCityCountry(): String = "${weatherForecast.value?.location?.name}\n${weatherForecast.value?.location?.country}"

    fun getCurrentTime(): String {
        val dateTime = "${
            weatherForecast.value?.location?.localtime_epoch?.let {
                this.getDateTime(it, DATE_AND_TIME_FORMAT)
            }
        }"
        return dateTime
    }

    fun getCurrentTemperature(): String = "${weatherForecast.value?.current?.temp_f?.toInt()} F"

    fun getCurrentMinMaxTemperature(): String = "Min: ${weatherForecast.value?.forecast?.forecastday?.first()?.day?.mintemp_f?.toInt()}  / Max: ${weatherForecast.value?.forecast?.forecastday?.first()?.day?.maxtemp_f?.toInt()}"

    fun getCurrentWeatherIconURL(): String {
        weatherForecast.value?.current?.condition?.icon?.let {
            return HTTPS_PREFIX + it.removeRange(0,2)
        }
        return ""
    }

    fun getCurrentWeatherCondition(): String = "${weatherForecast.value?.current?.condition?.text}"

    fun getHourlyForecast(): List<HourWeather> {
        weatherForecast.value?.forecast?.forecastday?.first()?.hour?.let {
            return it
        }
        return listOf()
    }

    fun getHourOfTheDay(index: Int): String {
        val time = "${
            weatherForecast.value?.forecast?.forecastday?.first()?.hour?.get(index)?.let {
                it.time_epoch?.let { it1 ->
                    this.getDateTime(it1, TIME_ONLY_FORMAT)
                }
            }}"
        return time
    }

    fun getTemperatureForTheHour(index: Int): String {
        return "${weatherForecast.value?.forecast?.forecastday?.first()?.hour?.get(index)?.temp_f?.toInt()}"
    }

    fun getWeatherIconForTheHour(index: Int): String {
        weatherForecast.value?.forecast?.forecastday?.first()?.hour?.get(index)?.condition?.icon?.let {
            return HTTPS_PREFIX + it.removeRange(0,2)
        }
        return ""
    }

    fun getWeatherConditionForTheHour(index: Int): String {
        return "${weatherForecast.value?.forecast?.forecastday?.first()?.hour?.get(index)?.condition?.text}"
    }

    fun getChanceOfPPTForTheHour(index: Int): String {
        return "${weatherForecast.value?.forecast?.forecastday?.first()?.hour?.get(index)?.chance_of_snow}"
    }

    fun fetchWeatherData(query: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            //This is just to let the user see the progress indicator for a while...
            delay(PROGRESS_INDICATOR_DELAY)
            val weatherForecastResult = repository.getWeatherForecast(query)
            _weatherForecast.postValue(weatherForecastResult)
            _isLoading.postValue(false)

        }
    }

    fun checkLocationPermissions(activity: Activity, context: Context) {
        LocationManager.instance.checkLocationPermissions(activity, context)
    }

    fun getLocation(activity: Activity, context: Context) {
        LocationManager.instance.getLocation(activity, context)
    }

    private fun getDateTime(epochSeconds: Long, preferredFormat: String): String {
        val date = Date(epochSeconds * 1000)
        val dateFormat = SimpleDateFormat(preferredFormat, Locale.getDefault())
        return dateFormat.format(date)
    }
}