package ru.vodolatskii.movies.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.models.Doc
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.fragments.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    fun getBinding() = binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()

        binding = ActivityMainBinding.inflate(layoutInflater)

        val root = binding.root

        setContentView(root)

        setClickListeners()

    }

    fun launchDetailsFragment(doc: Doc) {
        val bundle = Bundle()
        bundle.putParcelable("doc", doc)

        navController.navigate(R.id.detailsFragment, bundle)

//        val fragment = DetailsFragment()
//        fragment.arguments = bundle
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
    }

    private fun setClickListeners() {
//        binding.buttonPostersError.setOnClickListener {
//            viewModel.loadPosters()
//        }

        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT)
                .show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.button_search -> {
                    Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.favorites -> {
                    navController.navigate(R.id.favoriteFragment)
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть похже", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.getBackStackEntryCount()
        if (count <= 1) {

            AlertDialog.Builder(this)
                .setTitle("Вы хотите выйти?")
                .setPositiveButton("Да") { _, _ ->
                    finish()
                }
                .setNegativeButton("Нет") { _, _ ->
                }
                .show()

        } else {
            super.onBackPressed()
        }
    }

    private fun checkForInternet(context: Context): InternetType {

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
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return InternetType.NONE
            @Suppress("DEPRECATION")
            return InternetType.MOBILE //  xaxaxaxa
        }
    }
}

private enum class InternetType {
    WIFI, MOBILE, NONE
}

