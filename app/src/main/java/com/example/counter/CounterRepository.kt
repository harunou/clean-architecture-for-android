package com.example.counter

import kotlinx.coroutines.flow.Flow

interface CounterRepository {
    fun observe(): Flow<Counter>
    fun increment()
    fun decrement()
}
