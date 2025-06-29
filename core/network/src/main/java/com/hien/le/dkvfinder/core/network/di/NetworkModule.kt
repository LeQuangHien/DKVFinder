package com.hien.le.dkvfinder.core.network.di

import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.hien.le.dkvfinder.core.network.BuildConfig
import com.hien.le.dkvfinder.core.network.NetworkDataSource
import com.hien.le.dkvfinder.core.network.retrofit.RetrofitNetworkDataSource
import com.hien.le.dkvfinder.core.network.service.OpenChargeMapApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.openchargemap.io/v3/"

    @Provides
    @Singleton
    fun provideCommonInterceptor(): Interceptor {
        return Interceptor { chain ->
            val reqBuilder =
                chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("X-API-Key", BuildConfig.API_KEY)
            chain.proceed(reqBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(commonInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
            }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(commonInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(EitherCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenChargeMapApiService(retrofit: Retrofit): OpenChargeMapApiService {
        return retrofit.create(OpenChargeMapApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingsModule {
    @Binds
    @Singleton
    abstract fun bindNetworkDataSource(impl: RetrofitNetworkDataSource): NetworkDataSource
}
