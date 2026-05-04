package com.example.counter

class DecrementCounterUseCase(private val repository: CounterRepository) {
    operator fun invoke() = repository.decrement()
}
