package com.cra.sample.feature.counter.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCounterRepository
@Inject
constructor() : CounterRepository {
    private val _counter = MutableStateFlow(CounterEntity())

    override val counter: Flow<CounterEntity> = _counter.asStateFlow()

    override suspend fun increment(amount: Int) {
        _counter.update { it.copy(value = it.value + amount) }
    }

    override suspend fun decrement(amount: Int) {
        _counter.update { it.copy(value = it.value - amount) }
    }
}
