package com.example.counter

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CounterModule {
    @Binds
    abstract fun bindCounterRepository(impl: CounterRepositoryImpl): CounterRepository
}
