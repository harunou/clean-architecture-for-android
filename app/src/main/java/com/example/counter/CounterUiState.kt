package com.example.counter

enum class CounterLabel { POSITIVE, NEGATIVE, ZERO }

data class CounterUiState(
    val count: Int = 0,
    val label: CounterLabel = CounterLabel.ZERO,
)
