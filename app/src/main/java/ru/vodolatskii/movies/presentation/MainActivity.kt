package ru.vodolatskii.movies.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.common.AlarmsReceiver
import ru.vodolatskii.movies.common.AppReceiver
import ru.vodolatskii.movies.common.NotificationsReceiver
import ru.vodolatskii.movies.databinding.ActivityMainBinding
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.viewmodels.MoviesViewModel
import timber.log.Timber
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var appReceiver: BroadcastReceiver
    private lateinit var notificationReceiver: NotificationsReceiver
    private lateinit var alarmReceiver: AlarmsReceiver

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: MoviesViewModel

    private val PERMISSION_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissionAndGetPhoneNumber()


        App.instance.dagger.inject(this)
        viewModel = viewModelFactory.create(MoviesViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawerMenu()
        setupObservers()
        setupClickListeners()
        registerReceivers()

        val movie = intent.getParcelableExtra<Movie>("movie")
        if (movie != null) {
            passDataForDetailsFragment(movie)
        }
    }

    private fun registerReceivers() {
        Timber.d("registerReceivers -- ${this.packageName}.FIND")
        appReceiver = AppReceiver()
        val intentFilters = IntentFilter(Intent.ACTION_BATTERY_LOW)
        intentFilters.addAction(Intent.ACTION_POWER_CONNECTED)
        registerReceiver(appReceiver, intentFilters)

        notificationReceiver = NotificationsReceiver()
        val intentFilters1 = IntentFilter("${this.packageName}.FIND")
        intentFilters1.addAction("${this.packageName}.CANCEL")
        registerReceiver(notificationReceiver, intentFilters1)

        alarmReceiver = AlarmsReceiver()
        val intentFilters2 = IntentFilter(ACTION)
        registerReceiver(alarmReceiver, intentFilters2, RECEIVER_NOT_EXPORTED)
    }

    private fun setupDrawerMenu() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val toolBar: Toolbar = binding.topAppBar

        navController = findNavController(R.id.my_nav_host_fragment)

        setSupportActionBar(toolBar)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close)

        actionBarDrawerToggle.isDrawerIndicatorEnabled = false

        actionBarDrawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            when (navController.currentDestination?.id) {
                R.id.storageMenuFragment, R.id.storageRVFragment, R.id.settingsFragment -> {
                    navController.navigateUp()
                }

                else -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.detailsFragment, R.id.favoriteFragment, R.id.afterFragment, R.id.homeFragment -> {
                    actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
                }

                R.id.storageMenuFragment, R.id.storageRVFragment, R.id.settingsFragment -> {
                    actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
                }

                else -> {}
            }
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        navView.setupWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Timber.d("dest - $destination")
        }
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
                    navController.navigate(R.id.storageMenuFragment)
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

    private fun setupObservers() {
        viewModel.isSearchViewVisible.observe(this) { state ->
            binding.topAppBar.visibility = if (state) View.GONE else View.VISIBLE
        }

        viewModel.messageSingleLiveEvent.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun passDataForDetailsFragment(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable("movie", movie)

        navController.navigate(
            R.id.detailsFragment,
            bundle,
            null,
            null
        )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(appReceiver)
        unregisterReceiver(notificationReceiver)
        super.onDestroy()
    }


    private fun checkPermissionAndGetPhoneNumber() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_NUMBERS), PERMISSION_REQUEST_CODE)
        } else {
            getPhoneNumber()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoneNumber()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhoneNumber() {
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val phoneNumber = telephonyManager.line1Number
        if (!phoneNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Phone number: $phoneNumber", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_LONG).show()
        }
    }


    companion object {
        const val ACTION = "alarm_action"
    }
}



