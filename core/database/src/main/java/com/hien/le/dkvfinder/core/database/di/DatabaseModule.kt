package com.hien.le.dkvfinder.core.database.di

import android.content.Context
import androidx.room.Room
import com.hien.le.dkvfinder.core.database.AppDatabase
import com.hien.le.dkvfinder.core.database.dao.PoiDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "dkv_finder_database",
        ).build()
    }

    @Provides
    @Singleton
    fun providePoiDao(appDatabase: AppDatabase): PoiDao {
        return appDatabase.poiDao()
    }
}
