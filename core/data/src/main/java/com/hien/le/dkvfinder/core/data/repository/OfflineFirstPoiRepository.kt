package com.hien.le.dkvfinder.core.data.repository

import android.util.Log
import com.hien.le.dkvfinder.core.common.network.ConnectivityChecker
import com.hien.le.dkvfinder.core.common.network.DKVDispatchers
import com.hien.le.dkvfinder.core.common.network.Dispatcher
import com.hien.le.dkvfinder.core.database.dao.PoiDao
import com.hien.le.dkvfinder.core.model.data.PoiCompact
import com.hien.le.dkvfinder.core.model.data.asDataModel
import com.hien.le.dkvfinder.core.model.data.asEntity
import com.hien.le.dkvfinder.core.model.data.mapToPoiCompact
import com.hien.le.dkvfinder.core.network.NetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineFirstPoiRepository
    @Inject
    constructor(
        private val networkDataSource: NetworkDataSource,
        private val poiDao: PoiDao,
        private val connectivityChecker: ConnectivityChecker,
        @Dispatcher(DKVDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : PoiRepository {
        companion object {
            private const val TAG = "OfflineFirstPoiRepository"
        }

        /**
         * The stream of POIs is always sourced from the local database (PoiDao).
         * The PoiDao exposes a Flow, so any updates to the DB will automatically
         * be emitted to observers of this Flow.
         */
        override fun getPoisStream(): Flow<List<PoiCompact>> {
            return poiDao.getPoisStream()
                .map { poiEntities ->
                    poiEntities.map { it.asDataModel() }
                }
        }

        /**
         * Fetches POIs from the network if online and updates the local database.
         */
        override suspend fun refreshPois(
            country: String,
            maxResults: Int,
        ): Boolean {
            return withContext(ioDispatcher) {
                if (!connectivityChecker.isOnline()) {
                    // Optionally log or inform the user that they are offline
                    return@withContext false // Indicate refresh failed due to no connectivity
                }

                try {
                    val networkResult = networkDataSource.getListPoi(country, maxResults)
                    networkResult.fold(
                        ifLeft = { callError ->
                            Log.e(TAG, "Network refresh failed: $callError")
                            false // Indicate refresh failed
                        },
                        ifRight = { networkPois ->
                            // Map to compact list to simplify the data
                            val poiCompactList = networkPois.map { it.mapToPoiCompact() }
                            // Insert/update all. Room's @Insert(onConflict = OnConflictStrategy.REPLACE)
                            // can handle updates if the primary keys match.
                            poiDao.insertPois(poiCompactList.map { it.asEntity() })
                            true // Indicate refresh was successful
                        },
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during POI refresh: ${e.message}")
                    false // Indicate refresh failed
                }
            }
        }

        /**
         * Updates the favorite status of a POI in the local database.
         */
        override suspend fun updateFavoriteStatus(
            poiId: Int,
            isFavorite: Boolean,
        ) {
            withContext(ioDispatcher) {
                poiDao.updateFavoriteStatus(poiId, isFavorite)
            }
        }
    }
