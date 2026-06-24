package com.cra.sample.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@RequiresOptIn(
    message = "Only ProvideInput is allowed to call this.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalInputApi

class Input<T>(initialValue: T) {
    private val _state = MutableStateFlow(initialValue)
    private var isProvided = false

    @InternalInputApi
    fun provide(value: T) {
        isProvided = true
        _state.value = value
    }

    val flow: StateFlow<T> = _state.asStateFlow()

    val flowValue: T
        get() {
            if (!isProvided) {
                throw IllegalStateException(
                    "Input accessed before being provided by the UI.",
                )
            }
            return _state.value
        }
}

@OptIn(InternalInputApi::class)
@Composable
fun <T> ProvideInput(input: Input<T>, value: T) {
    SideEffect {
        input.provide(value)
    }
}
