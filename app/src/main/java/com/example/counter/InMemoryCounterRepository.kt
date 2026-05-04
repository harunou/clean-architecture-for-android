package com.example.counter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCounterRepository @Inject constructor() : CounterRepository {
    private val _counter = MutableStateFlow(Counter())

    override fun observe(): Flow<Counter> = _counter.asStateFlow()
    override fun increment() = _counter.update { it.copy(value = it.value + 1) }
    override fun decrement() = _counter.update { it.copy(value = it.value - 1) }
}
