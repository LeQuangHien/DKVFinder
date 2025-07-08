package com.hien.le.dkvfinder

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.hien.le.dkvfinder.core.navigation.NavigationManager
import com.hien.le.dkvfinder.core.navigation.NavigationRoute
import com.hien.le.dkvfinder.databinding.ActivityMainBinding
import com.hien.le.dkvfinder.feature.evcharging.poi.PoiFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        observeNavigationEvents(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun observeNavigationEvents(navController: NavController) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NavigationManager.navigationEvents.collectLatest { route ->
                    Log.d("MainActivity", "Navigation event received: $route")
                    handleNavigationWithSafeArgs(route, navController)
                }
            }
        }
    }


    private fun handleNavigationWithSafeArgs(route: NavigationRoute, navController: NavController) {
        try {
            when (route) {
                is NavigationRoute.ToMap -> {
                    val action = PoiFragmentDirections.actionPoiFragmentInToMapFragment(
                        route.latitude,
                        route.longitude
                    )
                    navController.navigate(action)
                }

                is NavigationRoute.ToPoiDetails -> {
                    val action = PoiFragmentDirections.actionPoiFragmentToPoiDetailsWebviewFragment(
                        route.poiId
                    )
                    navController.navigate(action)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Safe Args Navigation failed for route $route.", e)
        }
    }
}
