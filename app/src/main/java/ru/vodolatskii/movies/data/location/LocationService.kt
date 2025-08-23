package ru.vodolatskii.movies.data.location

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import androidx.core.content.ContextCompat

class LocationService : Service() {

    private val binder = LocationBinder()
    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun startLocationUpdates(listener: LocationListener) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            locationListener = listener

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L * 20,
                    0f,
                    listener
                )
            }

//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    1000L * 30,
//                    0f,
//                    listener
//                )
//            }
        }
    }

    private fun stopLocationUpdates() {
        locationListener?.let { listener ->
            locationManager.removeUpdates(listener)
            locationListener = null
        }
    }

//    fun getLastKnownLocation(): Location? {
//        return if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        } else {
//            null
//        }
//    }

    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
}