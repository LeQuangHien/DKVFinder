package com.hien.le.dkvfinder.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hien.le.dkvfinder.core.database.entity.PoiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Query("SELECT * FROM pois")
    fun getPoisStream(): Flow<List<PoiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPois(pois: List<PoiEntity>)

    @Query("UPDATE pois SET isFavorite = :isFavorite WHERE id = :poiId")
    suspend fun updateFavoriteStatus(
        poiId: Int,
        isFavorite: Boolean,
    )

    @Query("DELETE FROM pois")
    suspend fun deleteAllPois()
}
