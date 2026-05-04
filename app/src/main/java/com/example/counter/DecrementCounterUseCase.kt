package com.example.counter

import javax.inject.Inject

class DecrementCounterUseCase @Inject constructor(private val repository: CounterRepository) {
    operator fun invoke() = repository.decrement()
}
