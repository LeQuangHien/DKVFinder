package com.hien.le.dkvfinder.core.data.di

import com.hien.le.dkvfinder.core.data.repository.OfflineFirstPoiRepository
import com.hien.le.dkvfinder.core.data.repository.PoiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPoiRepository(
        impl: OfflineFirstPoiRepository
    ): PoiRepository
}