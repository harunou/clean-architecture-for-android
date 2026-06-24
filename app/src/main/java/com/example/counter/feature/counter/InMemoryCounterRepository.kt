package com.example.counter.feature.counter

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
    private val _isLoading = MutableStateFlow(false)

    override val counter: Flow<CounterEntity> = _counter.asStateFlow()
    override val isLoading: Flow<Boolean> = _isLoading.asStateFlow()

    override suspend fun load() = Unit

    override suspend fun increment(amount: Int) = _counter.update {
        it.copy(
            value =
            it.value + amount,
        )
    }

    override suspend fun decrement(amount: Int) = _counter.update {
        it.copy(
            value =
            it.value - amount,
        )
    }
}
