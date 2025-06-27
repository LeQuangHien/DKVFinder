package com.hien.le.dkvfinder.core.common.network.di

import com.hien.le.dkvfinder.core.common.network.ConnectivityChecker
import com.hien.le.dkvfinder.core.common.network.DefaultConnectivityChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityChecker(
        impl: DefaultConnectivityChecker
    ): ConnectivityChecker
}