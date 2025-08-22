package ru.vodolatskii.movies.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.LocationService
import ru.vodolatskii.movies.databinding.ActivityLaunchBinding
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
import timber.log.Timber
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MoviesViewModel

    private lateinit var binding: ActivityLaunchBinding

    private lateinit var myService: LocationService
    private var isBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //Получаем объект IBinder и преобразуем его к MyService.LocalBinder
            val binder = service as LocationService.LocalBinder
            //При помощи метода getService() получаем ссылку на сам сервис
            myService = binder.getService()
            //выставляем флаг, что мы присоединились к сервису
            isBound = true
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            //Ставим флаг, что от сервиса отсоединились
            isBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        requestPermission()

        App.instance.dagger.inject(this)
        viewModel = viewModelFactory.create(MoviesViewModel::class.java)

        switchContentSource()
        startTVAnimation()

        if (!checkPermission()) {
            requestPermission()
            Intent(this, LocationService::class.java).also {
                bindService(it, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            Intent(this, LocationService::class.java).also {
                bindService(it, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
        return
    }

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }


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
