package com.example.counter

class AppContainer {
    private val counterRepository: CounterRepository = CounterRepositoryImpl()

    val getCounterUseCase = GetCounterUseCase(counterRepository)
    val incrementCounterUseCase = IncrementCounterUseCase(counterRepository)
    val decrementCounterUseCase = DecrementCounterUseCase(counterRepository)
}
