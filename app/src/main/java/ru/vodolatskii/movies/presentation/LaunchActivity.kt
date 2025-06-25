package ru.vodolatskii.movies.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.ActivityLaunchBinding
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


@SuppressLint("CustomSplashScreen")
class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchBinding
    private val viewModel: MoviesViewModel by viewModels {
        App.instance.dagger.viewModelsFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        App.instance.dagger.inject(this)
        switchContentSource()
        startTVAnimation()
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
                viewModel.putSource(SOURCE_INTERNET)
                Toast.makeText(this, "On Line", Toast.LENGTH_LONG).show()
                viewModel.getMoviesFromApi()
            }

            InternetType.MOBILE -> {
                viewModel.putSource(SOURCE_INTERNET)
                Toast.makeText(this, "On Line", Toast.LENGTH_LONG).show()
                viewModel.getMoviesFromApi()
            }

            InternetType.NONE -> {
                Toast.makeText(this, "Off Line", Toast.LENGTH_LONG).show()
                viewModel.putSource(SOURCE_STORAGE)
                viewModel.getMoviesFromStorage()
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
