package com.example.counter.feature.counter

import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    val counter: Flow<CounterEntity>
    val isLoading: Flow<Boolean>

    suspend fun load()

    suspend fun increment(amount: Int)

    suspend fun decrement(amount: Int)
}
