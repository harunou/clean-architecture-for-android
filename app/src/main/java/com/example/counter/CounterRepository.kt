package com.example.counter

import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun observe(): Flow<Counter>
    suspend fun load()
    suspend fun increment(amount: Int)
    suspend fun decrement(amount: Int)
}
