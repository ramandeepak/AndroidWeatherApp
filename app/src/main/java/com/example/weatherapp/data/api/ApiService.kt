package com.example.weatherapp.data.api

import com.example.weatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    companion object {
        const val API_KEY = "16309e007f0f40a5a80123044221507"
    }

    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") location: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Response<WeatherResponse>
}
