package com.hien.le.dkvfinder.core.network.service

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.hien.le.dkvfinder.core.model.data.Poi
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenChargeMapApiService {
    @GET("poi")
    suspend fun getListPoi(
        @Query("countrycode") country: String,
        @Query("maxresults") maxResults: Int = 100,
        @Query("compact") compact: Boolean = true,
        @Query("verbose") verbose: Boolean = false,
    ): Either<CallError, List<Poi>>
}
