package com.hien.le.dkvfinder.core.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.hien.le.dkvfinder.core.common.di.DKVDispatchers
import com.hien.le.dkvfinder.core.common.di.Dispatcher
import com.hien.le.dkvfinder.core.model.data.Poi
import com.hien.le.dkvfinder.core.network.NetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPoiRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    @Dispatcher(DKVDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) : PoiRepository {
    override suspend fun getListPoi(country: String, maxResults: Int) =
        withContext(defaultDispatcher) {
            networkDataSource.getListPoi(country, maxResults)
        }
}

interface PoiRepository {
    suspend fun getListPoi(country: String, maxResults: Int): Either<CallError, List<Poi>>
}