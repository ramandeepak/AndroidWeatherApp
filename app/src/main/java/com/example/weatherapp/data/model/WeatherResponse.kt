package com.example.weatherapp.data.model

data class WeatherResponse(
    val location: LocationData?,
    val current: CurrentWeather?,
    val forecast: Forecast?
)

data class LocationData(
    val name: String?,
    val country: String?,
    val tz_id: String?,
    val localtime_epoch: Long?,
    val localtime: String?
)

data class CurrentWeather(
    val temp_f: Double?,
    val condition: Condition?,
)

data class Forecast(
    val forecastday: List<ForecastDay>?
)

data class ForecastDay(
    val day: DayWeather?,
    val hour: List<HourWeather>?
)

data class DayWeather(
    val maxtemp_f: Double?,
    val mintemp_f: Double?,
)

data class HourWeather(
    val time_epoch: Long?,
    val time: String?,
    val temp_f: Double?,
    val condition: Condition?,
    val chance_of_rain: Int?,
    val chance_of_snow: Int?,
)

data class Condition(
    val text: String?,
    val icon: String?,
    val code: Int?
)