package com.example.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val getCounter: GetCounterUseCase,
    private val incrementCounter: IncrementCounterUseCase,
    private val decrementCounter: DecrementCounterUseCase,
) : ViewModel() {

    val uiState: StateFlow<CounterUiState> = getCounter()
        .map { counter ->
            CounterUiState(
                count = counter.value,
                label = when {
                    counter.value > 0 -> CounterLabel.POSITIVE
                    counter.value < 0 -> CounterLabel.NEGATIVE
                    else -> CounterLabel.ZERO
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CounterUiState(),
        )

    fun increment() = incrementCounter()
    fun decrement() = decrementCounter()
}
