package com.hien.le.dkvfinder.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class PoiEntity(
    @PrimaryKey val id: Int, // Assumes non-null ID from PoiItemUiState
    val title: String?,
    val address: String?,
    val town: String?,
    val telephone: String?,
    val distance: Double?,
    val distanceUnit: Int?,
    val isFavorite: Boolean = false,
)
