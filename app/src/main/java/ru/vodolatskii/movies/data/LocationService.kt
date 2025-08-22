package ru.vodolatskii.movies.data

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.ActivityCompat
import timber.log.Timber
import java.util.Random

class LocationService : Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var currentLocation: Location? = null

    override fun onBind(intent: Intent): IBinder {
        setupLocationManager()
        return binder
    }

    private val binder = LocalBinder()

    fun getLocation() = currentLocation

    private fun setupLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener { location ->
            currentLocation = location
            Timber.d("lat -- $location.latitude, long -- $location.longitude")
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            0f,
            locationListener
        )
    }


    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
}