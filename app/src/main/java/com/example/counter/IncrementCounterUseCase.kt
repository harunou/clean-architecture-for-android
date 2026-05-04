package com.example.counter

import javax.inject.Inject

class IncrementCounterUseCase @Inject constructor(private val repository: CounterRepository) {
    operator fun invoke() = repository.increment()
}
