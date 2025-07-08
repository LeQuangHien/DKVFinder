package com.hien.le.dkvfinder.core.navigation

sealed class NavigationRoute {
    data class ToMap(val latitude: Float, val longitude: Float) : NavigationRoute()
    data class ToPoiDetails(val poiId: Int) : NavigationRoute()
}
