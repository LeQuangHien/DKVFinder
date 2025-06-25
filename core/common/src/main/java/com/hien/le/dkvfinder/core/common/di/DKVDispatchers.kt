package com.hien.le.dkvfinder.core.common.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val dispatcher: DKVDispatchers)

enum class DKVDispatchers {
    Default,
    IO,
}