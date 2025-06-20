package ru.vodolatskii.movies.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.setupWithNavController
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val viewModel: MoviesViewModel by viewModels {
        App.instance.dagger.viewModelsFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.dagger.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.my_nav_host_fragment)

        binding.bottomNavigation.setupWithNavController(navController)

        setupObservers()

        setupClickListeners()
    }


    @Deprecated("Deprecated in Java")
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


    private fun setupObservers() {
        viewModel.isSearchViewVisible.observe(this) { state ->
            binding.topAppBarLayout.visibility = if (state) View.GONE else View.VISIBLE
        }
    }


    fun shareMoviesViewModel(): MoviesViewModel {
        return viewModel
    }


    fun launchDetailsFragment(movie: Movie, view: View) {
        val bundle = Bundle()
        bundle.putParcelable("movie", movie)

        val extras = FragmentNavigatorExtras(
            view to "text_transition_name"
        )

        navController.navigate(
            R.id.detailsFragment,
            bundle,
            null,
            extras
        )
    }


    private fun setupClickListeners() {
        binding.topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT)
                .show()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.button_search -> {
                    viewModel.switchSearchViewVisibility(true)
                    true
                }

                else -> false
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
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return InternetType.NONE
            @Suppress("DEPRECATION")
            return InternetType.MOBILE //  xaxaxaxa
        }
    }

    //    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.button_search) {
//            viewModel.switchSearchViewVisibility(true)
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//
//        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
//
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchView = menu.findItem(R.id.button_search).actionView as SearchView
//        val component = ComponentName(this, MainActivity::class.java)
//        val searchableInfo = searchManager.getSearchableInfo(component)
//        searchView.setSearchableInfo(searchableInfo)
//        return true
//    }
}

private enum class InternetType {
    WIFI, MOBILE, NONE
}

