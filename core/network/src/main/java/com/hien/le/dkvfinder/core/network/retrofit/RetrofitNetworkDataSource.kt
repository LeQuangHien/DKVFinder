package com.hien.le.dkvfinder.core.network.retrofit

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.hien.le.dkvfinder.core.model.data.Poi
import com.hien.le.dkvfinder.core.network.NetworkDataSource
import com.hien.le.dkvfinder.core.network.service.OpenChargeMapApiService
import jakarta.inject.Inject

class RetrofitNetworkDataSource @Inject constructor(
    private val service: OpenChargeMapApiService
) : NetworkDataSource {
    override suspend fun getListPoi(
        country: String,
        maxResults: Int
    ): Either<CallError, List<Poi>> =
        service.getListPoi(country, maxResults)
}