package com.example.weatherapp.utils.network

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices

class LocationManager private constructor() {

    companion  object {
        val instance: LocationManager by lazy {
            LocationManager()
        }
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val _geoCoordinates = MutableLiveData<Pair<Double, Double>>()
    val geoCoordinates: LiveData<Pair<Double, Double>> = _geoCoordinates

    fun checkLocationPermissions(activity: Activity, context: Context) {
        // Check if permissions are granted

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted, get the location
            getLocation(activity, context)
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    fun getLocation(activity: Activity, context: Context) {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _geoCoordinates.postValue(Pair(location.latitude, location.longitude))
            } else {
                // Location not found, handle accordingly
            }
        }
    }
}
