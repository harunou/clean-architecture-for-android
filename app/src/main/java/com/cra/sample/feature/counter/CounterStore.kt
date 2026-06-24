package com.cra.sample.feature.counter

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Scope to the counter navigation destination once Navigation Compose is introduced
@ActivityRetainedScoped
class CounterStore
@Inject
constructor() {
    private val _incrementAmount = MutableStateFlow(1f)
    val incrementAmount: StateFlow<Float> = _incrementAmount.asStateFlow()

    fun setIncrementAmount(value: Float) {
        _incrementAmount.value = value
    }
}
