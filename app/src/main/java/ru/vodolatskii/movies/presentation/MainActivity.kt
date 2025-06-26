package ru.vodolatskii.movies.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    val viewModel: MoviesViewModel by viewModels {
        App.instance.dagger.viewModelsFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.dagger.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawerMenu()
        setupObservers()
        setupClickListeners()
    }

    private fun setupDrawerMenu() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val toolBar: Toolbar = binding.topAppBar
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navController = findNavController(R.id.my_nav_host_fragment)
        navView.setupWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        if (count <= 1) {

            AlertDialog.Builder(this)
                .setTitle(R.string.exit_app)
                .setPositiveButton(R.string.yes) { _, _ ->
                    finish()
                }
                .setNegativeButton(R.string.no) { _, _ ->
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
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
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
                R.id.storageFragment -> {
                    navController.navigate(R.id.storageFragment)
                    true
                }
                R.id.exit -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.exit_app)
                        .setIcon(R.drawable.baseline_warning_24)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            finish()
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                        }
                        .show()
                    false
                }

                else -> false
            }
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
}



