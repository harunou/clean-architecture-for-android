package com.example.counter.feature.counter

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Scope to the counter navigation destination once Navigation Compose is introduced
@ActivityRetainedScoped
class AmountStore
@Inject
constructor() {
    private val _sliderValue = MutableStateFlow(1f)
    val sliderValue: StateFlow<Float> = _sliderValue.asStateFlow()

    fun setSliderValue(value: Float) {
        _sliderValue.value = value
    }
}
