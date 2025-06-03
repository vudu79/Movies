package ru.vodolatskii.movies.presentation

import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.repository.impl.RepositoryProvider
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.fragments.HomeFragment
import java.util.Locale


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MoviesViewModel
    private lateinit var navController: NavController

    private val movieList = viewModel.cacheMovieList

    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setupViewModel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navController = findNavController(R.id.my_nav_host_fragment)

        binding.bottomNavigation.setupWithNavController(navController)

        setClickListeners()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.button_search) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.button_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView? = searchItem?.actionView as SearchView

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)

    }


    fun getMoviesViewModel(): MoviesViewModel {
        return viewModel
    }

    fun launchDetailsFragment(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable("movie", movie)
        navController.navigate(R.id.detailsFragment, bundle)
    }

    private fun setClickListeners() {

        binding
//        binding. .setNavigationOnClickListener {
//            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT)
//                .show()
//        }
//
//        binding.topAppBar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.button_search -> {
//                    Toast.makeText(this, "Поиск", Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                else -> false
//            }
//        }
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

    private fun setupViewModel() {
        val factory = MyViewModelFactory(RepositoryProvider.provideRepository())
        viewModel = ViewModelProvider(this, factory)[MoviesViewModel::class.java]
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    fun updateHomeFragmentAdapter(data: List<Movie>?) {
        // Получаем ссылку на фрагмент
        val controller = homeFragment

        // Вызываем метод интерфейса
        controller.updateAdapterData(data)
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d("mytag", "ggggggg")

        if (newText.isNullOrEmpty()) {
            updateHomeFragmentAdapter(movieList)
            return true
        } else {
            Log.d("mytag", "ggggggg")
            val result = movieList.filter {
                it.name.toLowerCase(Locale.getDefault())
                    .contains(newText.toLowerCase(Locale.getDefault()))

            }
            updateHomeFragmentAdapter(result)
            return true
        }
    }
}

private enum class InternetType {
    WIFI, MOBILE, NONE
}

