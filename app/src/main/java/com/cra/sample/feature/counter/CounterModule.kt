package com.cra.sample.feature.counter

import com.cra.sample.feature.counter.repository.CounterRepository
import com.cra.sample.feature.counter.repository.InMemoryCounterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CounterModule {
    @Binds
    abstract fun bindCounterRepository(impl: InMemoryCounterRepository): CounterRepository
}
