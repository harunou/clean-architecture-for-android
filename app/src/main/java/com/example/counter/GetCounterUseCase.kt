package com.example.counter

import kotlinx.coroutines.flow.Flow

class GetCounterUseCase(private val repository: CounterRepository) {
    operator fun invoke(): Flow<Counter> = repository.observe()
}
