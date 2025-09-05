package ru.vodolatskii.movies.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.location.LocationService
import ru.vodolatskii.movies.data.location.MyLocationListener
import ru.vodolatskii.movies.databinding.ActivityLaunchBinding
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
import java.util.UUID
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MoviesViewModel

    private lateinit var binding: ActivityLaunchBinding

    private var locationService: LocationService? = null
    private var bound = false
    private lateinit var locationListener: MyLocationListener

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()

    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startLocationService()
            }

            else -> {
                handlePermissionDenied()
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocationBinder
            locationService = binder.getService()
            bound = true
            startLocationUpdates()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        App.instance.dagger.inject(this)
        viewModel = viewModelFactory.create(MoviesViewModel::class.java)

        switchContentSource()

        locationListener = MyLocationListener()
        setupLocationListener()
        checkLocationPermission()

        startTVAnimation()
    }

    private fun setupLocationListener() {
        locationListener.onLocationChanged = { location ->
            handleLocationUpdate(location)
        }
        locationListener.onProviderEnabled = { provider ->
            handleProviderEnabled(provider)
        }
        locationListener.onProviderDisabled = { provider ->
            handleProviderDisabled(provider)
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationService()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Разрешение на местоположение")
            .setMessage("Приложению необходимо разрешение на доступ к местоположению для управления темой")
            .setPositiveButton("Предоставить") { _, _ ->
                requestLocationPermission()
            }
            .setNegativeButton("Отмена") { _, _ ->
                handlePermissionDenied()
            }
            .show()
    }

    private fun showLocationSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("GPS отключен")
            .setMessage("Для работы приложения необходимо включить GPS")
            .setPositiveButton("Настройки") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun startLocationService() {
        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun startLocationUpdates() {
        locationService?.let { service ->
            if (service.isLocationEnabled()) {
                service.startLocationUpdates(locationListener)
//                service.getLastKnownLocation()?.let { location ->
//                    handleLocationUpdate(location)
//                }
            } else {
                showLocationSettingsDialog()
            }
        }
    }

    private fun handleLocationUpdate(location: Location) {
        updateUI(location)
    }

    private fun updateUI(location: Location) {
        viewModel.updateLocation(location)
    }

    private fun handleProviderEnabled(provider: String) {
        Toast.makeText(this, "$provider GPS ia active", Toast.LENGTH_LONG).show()
    }

    private fun handleProviderDisabled(provider: String) {
        Toast.makeText(this, "$provider GPS is not active", Toast.LENGTH_LONG).show()
    }

    private fun handlePermissionDenied() {
        Toast.makeText(this, "Permission is not granted", Toast.LENGTH_LONG).show()
    }

//    override fun onDestroy() {
//        super.onDestroy()
////        if (bound) {
////            locationService?.stopLocationUpdates()
////            unbindService(connection)
////            bound = false
////        }
//    }


    private fun startTVAnimation() {
        val tvAnimation = AnimationUtils.loadAnimation(this, R.anim.tv_set_anim)
        binding.tv.startAnimation(tvAnimation)
        binding.tv.setOnClickListener {
            binding.tv.animate()
                .setDuration(1000)
                .rotation(720f)
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .withEndAction {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    private fun switchContentSource() {
        when (checkInternetStatus(this)) {
            InternetType.WIFI -> {
                viewModel.putContentSource(SOURCE_INTERNET)
                Toast.makeText(this, "On Line", Toast.LENGTH_LONG).show()
//                viewModel.getMoviesFromApi()
            }

            InternetType.MOBILE -> {
                viewModel.putContentSource(SOURCE_INTERNET)
                Toast.makeText(this, "On Line", Toast.LENGTH_LONG).show()
//                viewModel.getMoviesFromApi()
            }

            InternetType.NONE -> {
                Toast.makeText(this, "Off Line", Toast.LENGTH_LONG).show()
                viewModel.putContentSource(SOURCE_STORAGE)
//                viewModel.loadMoviesFromStorageInOffLine()
            }
        }
    }

    private fun checkInternetStatus(context: Context): InternetType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return InternetType.NONE
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return InternetType.NONE
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> InternetType.WIFI
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> InternetType.MOBILE
                else -> InternetType.NONE // xaxaxaxa
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo =
                connectivityManager.activeNetworkInfo ?: return InternetType.NONE
            @Suppress("DEPRECATION")
            return InternetType.MOBILE //  xaxaxaxa
        }
    }


    companion object {
        private const val SOURCE_INTERNET = "internet"
        private const val SOURCE_STORAGE = "storage"
    }
}

private enum class InternetType {
    WIFI, MOBILE, NONE
}
