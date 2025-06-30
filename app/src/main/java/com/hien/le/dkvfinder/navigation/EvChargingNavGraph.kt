package com.hien.le.dkvfinder.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hien.le.dkvfinder.feature.evcharging_compose.poi.PoiScreen
import com.hien.le.dkvfinder.feature.evcharging_compose.webview.PoiDetailsWebviewScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Details(val poiId: Int)

@Composable
fun EvChargingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home,
    ) {
        composable<Home> {
            PoiScreen(
                onItemClicked = { poiId ->
                    navController.navigate(Details(poiId))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
        composable<Details> { navBackStackEntry ->
            val details: Details = navBackStackEntry.toRoute()
            PoiDetailsWebviewScreen(
                poiId = details.poiId,
                onNavigateUp = navController::navigateUp,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
