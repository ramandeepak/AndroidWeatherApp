package com.example.weatherapp.data.repository

import com.example.weatherapp.data.model.WeatherResponse

interface WeatherRepository {
    suspend fun getWeatherForecast(location: String): WeatherResponse?
}