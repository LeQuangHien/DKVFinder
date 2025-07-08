package com.hien.le.dkvfinder.core.navigation

sealed class NavigationRoute {
    data class ToMap(val latitude: Double, val longitude: Double) : NavigationRoute()
    data class ToPoiDetails(val poiId: Int) : NavigationRoute()
}
