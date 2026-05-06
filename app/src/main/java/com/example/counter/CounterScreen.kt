package com.example.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounterScreenViewModel @Inject constructor(
    override val counterRepository: CounterRepository,
    override val initializeCounterUseCaseFactory: InitializeCounterUseCase.Factory,
) : ViewModel(), CounterViewModel

@Composable
fun CounterScreen(
    modifier: Modifier = Modifier,
    vm: CounterScreenViewModel = hiltViewModel(),
) {
    CompositionLocalProvider(LocalCounterViewModel provides vm) {
        Counter(modifier = modifier)
    }
}
