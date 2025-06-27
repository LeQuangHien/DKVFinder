package com.hien.le.dkvfinder.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hien.le.dkvfinder.core.database.dao.PoiDao
import com.hien.le.dkvfinder.core.database.entity.PoiEntity

@Database(entities = [PoiEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun poiDao(): PoiDao
}