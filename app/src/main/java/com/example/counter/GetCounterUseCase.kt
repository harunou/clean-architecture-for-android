package com.example.counter

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCounterUseCase @Inject constructor(private val repository: CounterRepository) {
    operator fun invoke(): Flow<Counter> = repository.observe()
}
