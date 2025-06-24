package com.hien.le.dkvfinder.core.network.service

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.hien.le.dkvfinder.core.model.data.Poi
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenChargeMapApiService {
    @GET("poi")
    suspend fun getListPoi(
        @Query("country") country: String,
    ): Either<CallError, List<Poi>>
}