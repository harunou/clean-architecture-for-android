package com.example.counter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounterScreenViewModel @Inject constructor(
    val counterRepository: CounterRepository,
    val initializeCounterUseCaseFactory: InitializeCounterUseCase.Factory,
) : ViewModel()

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    Counter(modifier = modifier)
}
