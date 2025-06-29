package com.hien.le.dkvfinder.core.data.repository

import com.hien.le.dkvfinder.core.model.data.PoiCompact
import kotlinx.coroutines.flow.Flow

interface PoiRepository {
    /**
     * Gets a Flow of the list of POIs. The Flow will emit new lists
     * whenever the underlying data in the local cache changes.
     */
    fun getPoisStream(): Flow<List<PoiCompact>>

    /**
     * Attempts to refresh the POI list from the network for the given country
     * and update the local cache.
     * @return true if refresh was successful, false otherwise (e.g., network error).
     */
    suspend fun refreshPois(
        country: String,
        maxResults: Int,
    ): Boolean

    /**
     * Updates the favorite status of a POI in the local cache.
     */
    suspend fun updateFavoriteStatus(
        poiId: Int,
        isFavorite: Boolean,
    )
}
