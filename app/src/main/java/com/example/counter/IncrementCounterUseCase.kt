package com.example.counter

class IncrementCounterUseCase(private val repository: CounterRepository) {
    operator fun invoke() = repository.increment()
}
