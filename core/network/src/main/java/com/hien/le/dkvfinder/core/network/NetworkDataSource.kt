package com.hien.le.dkvfinder.core.network

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.hien.le.dkvfinder.core.model.data.Poi

interface NetworkDataSource {
    suspend fun getListPoi(
        country: String,
        maxResults: Int,
    ): Either<CallError, List<Poi>>
}
