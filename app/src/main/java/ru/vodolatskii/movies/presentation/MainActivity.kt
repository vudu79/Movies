package ru.vodolatskii.movies.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var  actionBarDrawerToggle: ActionBarDrawerToggle

    val viewModel: MoviesViewModel by viewModels {
        App.instance.dagger.viewModelsFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.dagger.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout : DrawerLayout = binding.drawerLayout
        val navView : NavigationView = binding.navView
        val toolBar : Toolbar = binding.topAppBar

//        setSupportActionBar(toolBar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close )
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navController = findNavController(R.id.my_nav_host_fragment)

//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.settingsFragment
//            ), drawerLayout
//        )
//
//        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

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

    override fun onSupportNavigateUp(): Boolean {
       val navController = findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // check conndition for drawer item with menu item
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }


    private fun setupObservers() {
        viewModel.isSearchViewVisible.observe(this) { state ->
            binding.topAppBar.visibility = if (state) View.GONE else View.VISIBLE
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

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }

                else -> false
            }
        }
//
//        binding.topAppBar.setNavigationOnClickListener {
//            Toast.makeText(this, "Будет дополнительная навигация с настройками", Toast.LENGTH_SHORT)
//                .show()
//        }

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
}

private enum class InternetType {
    WIFI, MOBILE, NONE
}

