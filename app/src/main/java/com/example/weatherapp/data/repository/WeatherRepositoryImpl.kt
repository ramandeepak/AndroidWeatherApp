package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.api.ApiService
import com.example.weatherapp.utils.network.RetrofitInstance
import com.example.weatherapp.data.model.WeatherResponse

class WeatherRepositoryImpl(): WeatherRepository {
    private val apiService = RetrofitInstance.retrofit.create(ApiService::class.java)

    override suspend fun getWeatherForecast(location: String): WeatherResponse? {
        try {
            val response = apiService.getWeatherForecast(location = location)
            return if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (exception: Exception) {
            Log.d("WeatherAPIException", exception.toString())
            return null
        }
    }
}