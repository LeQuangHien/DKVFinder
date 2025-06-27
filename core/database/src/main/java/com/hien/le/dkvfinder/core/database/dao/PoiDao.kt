package com.hien.le.dkvfinder.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hien.le.dkvfinder.core.database.entity.PoiEntity

@Dao
interface PoiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPois(pois: List<PoiEntity>)

    @Query("SELECT * FROM pois")
    fun getAllPois(): LiveData<List<PoiEntity>>

    @Query("SELECT * FROM pois WHERE isFavorite = 1")
    fun getFavoritePois(): LiveData<List<PoiEntity>>

    @Query("UPDATE pois SET isFavorite = :isFavorite WHERE id = :poiId")
    suspend fun updateFavoriteStatus(poiId: Int, isFavorite: Boolean)

    @Query("DELETE FROM pois")
    suspend fun deleteAllPois()
}