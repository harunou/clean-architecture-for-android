package com.cra.sample.feature.counter.repository

import kotlinx.coroutines.flow.Flow

data class CounterEntity(val value: Int = 0)

interface CounterRepository {
    val counter: Flow<CounterEntity>

    suspend fun increment(amount: Int)

    suspend fun decrement(amount: Int)
}
