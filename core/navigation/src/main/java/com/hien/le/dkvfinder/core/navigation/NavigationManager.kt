package com.hien.le.dkvfinder.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Singleton object to manage and dispatch navigation requests across modules.
 */
object NavigationManager {
    // Use a CoroutineScope that lives as long as the application
    private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val _navigationEvents = MutableSharedFlow<NavigationRoute>(
        extraBufferCapacity = 1 // handle rapid emissions
    )
    val navigationEvents = _navigationEvents.asSharedFlow()

    /**
     * Call this from feature modules to request navigation.
     */
    fun navigate(route: NavigationRoute) {
        scope.launch {
            _navigationEvents.emit(route)
        }
    }
}
