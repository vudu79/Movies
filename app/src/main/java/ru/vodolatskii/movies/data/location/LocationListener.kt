package ru.vodolatskii.movies.data.location

import android.location.Location
import android.location.LocationListener

class MyLocationListener : LocationListener {

    var onLocationChanged: ((Location) -> Unit)? = null
    var onProviderEnabled: ((String) -> Unit)? = null
    var onProviderDisabled: ((String) -> Unit)? = null

    override fun onLocationChanged(location: Location) {
        onLocationChanged?.invoke(location)
    }

    override fun onProviderEnabled(provider: String) {
        onProviderEnabled?.invoke(provider)
    }

    override fun onProviderDisabled(provider: String) {
        onProviderDisabled?.invoke(provider)
    }
}