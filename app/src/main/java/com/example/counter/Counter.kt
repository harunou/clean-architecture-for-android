package com.example.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.counter.ui.theme.CounterTheme
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface CounterViewModel {
    val counterRepository: CounterRepository
    val initializeCounterUseCaseFactory: InitializeCounterUseCase.Factory
}

object CounterViewModelMock : CounterViewModel {
    override val counterRepository: CounterRepository get() = error("not available in preview")
    override val initializeCounterUseCaseFactory: InitializeCounterUseCase.Factory get() = error("not available in preview")
}

val LocalCounterViewModel = compositionLocalOf<CounterViewModel> { CounterViewModelMock }

val LocalSliderValue = compositionLocalOf { MutableStateFlow(1f) }

class InitializeCounterUseCase @AssistedInject constructor(
    private val repository: CounterRepository,
    @Assisted private val isCounterLoadingFlow: MutableStateFlow<Boolean>?,
) : UseCase<Unit> {
    @AssistedFactory
    interface Factory {
        fun make(isCounterLoadingFlow: MutableStateFlow<Boolean>?): InitializeCounterUseCase
    }

    override suspend fun execute(params: Unit) {
        isCounterLoadingFlow?.value = true
        repository.load()
        isCounterLoadingFlow?.value = false
    }
}

interface CounterPresenter {
    val count: String
    val countLabel: String
    val isLoading: Boolean
}

interface CounterController {
    fun onCounterComposableLaunched()
    fun onPlusButtonClick()
    fun onMinusButtonClick()
}

object CounterPresenterMock : CounterPresenter {
    override val count = "0"
    override val countLabel = "Zero"
    override val isLoading = false
}

object CounterControllerMock : CounterController {
    override fun onCounterComposableLaunched() {}
    override fun onPlusButtonClick() {}
    override fun onMinusButtonClick() {}
}

val LocalCounterPresenter = compositionLocalOf<CounterPresenter> {
    CounterPresenterMock
}

val LocalCounterController = compositionLocalOf<CounterController> {
    CounterControllerMock
}

@Composable
fun makeCounterPresenter(
    vm: CounterViewModel,
    isCounterLoadingFlow: StateFlow<Boolean>,
): CounterPresenter {
    if (LocalInspectionMode.current) return LocalCounterPresenter.current

    val scope = rememberCoroutineScope()

    val counter = remember(vm) {
        vm.counterRepository.observe()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CounterEntity(),
            )
    }.collectAsStateWithLifecycle()

    val isLoading = isCounterLoadingFlow.collectAsStateWithLifecycle()

    return remember(vm) {
        object : CounterPresenter {
            override val count: String get() = counter.value.value.toString()
            override val countLabel: String get() = when {
                counter.value.value > 0 -> "Positive"
                counter.value.value < 0 -> "Negative"
                else -> "Zero"
            }
            override val isLoading: Boolean get() = isLoading.value
        }
    }
}

@Composable
fun makeCounterController(
    vm: CounterViewModel,
    isCounterLoadingFlow: MutableStateFlow<Boolean>,
    sliderValueFlow: MutableStateFlow<Float>,
): CounterController {
    if (LocalInspectionMode.current) return LocalCounterController.current

    val initializeCounterUseCase = vm.initializeCounterUseCaseFactory.make(isCounterLoadingFlow)
    val scope = rememberCoroutineScope()

    return remember(vm) {
        object : CounterController {
            override fun onCounterComposableLaunched() { scope.launch { initializeCounterUseCase.execute() } }
            override fun onPlusButtonClick() {
                scope.launch {
                    isCounterLoadingFlow.value = true
                    vm.counterRepository.increment(sliderValueFlow.value.toInt())
                    isCounterLoadingFlow.value = false
                }
            }
            override fun onMinusButtonClick() {
                scope.launch {
                    isCounterLoadingFlow.value = true
                    vm.counterRepository.decrement(sliderValueFlow.value.toInt())
                    isCounterLoadingFlow.value = false
                }
            }
        }
    }
}

@Composable
fun Counter(modifier: Modifier = Modifier) {
    val vm = LocalCounterViewModel.current
    val isCounterLoadingFlow = remember { MutableStateFlow(false) }
    val sliderValue = remember { MutableStateFlow(1f) }
    val presenter = makeCounterPresenter(vm, isCounterLoadingFlow)
    val controller = makeCounterController(vm, isCounterLoadingFlow, sliderValue)

    LaunchedEffect(controller) { controller.onCounterComposableLaunched() }

    CompositionLocalProvider(LocalSliderValue provides sliderValue) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = presenter.count,
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    text = presenter.countLabel,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = controller::onMinusButtonClick) { Text("-") }
                    Button(onClick = controller::onPlusButtonClick) { Text("+") }
                }
                Spacer(modifier = Modifier.height(16.dp))
                CounterAmountInput()
            }
            if (presenter.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Zero")
@Composable
private fun CounterZeroPreview() {
    CounterTheme {
        Surface {
            Counter()
        }
    }
}

@Preview(showBackground = true, name = "Positive")
@Composable
private fun CounterPositivePreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterPresenter provides object : CounterPresenter {
                    override val count = "5"
                    override val countLabel = "Positive"
                    override val isLoading = false
                },
                LocalCounterAmountInputPresenter provides object : CounterAmountInputPresenter {
                    override val sliderValue = 5f
                    override val amount = "5"
                },
            ) {
                Counter()
            }
        }
    }
}

@Preview(showBackground = true, name = "Negative")
@Composable
private fun CounterNegativePreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterPresenter provides object : CounterPresenter {
                    override val count = "-3"
                    override val countLabel = "Negative"
                    override val isLoading = false
                },
                LocalCounterAmountInputPresenter provides object : CounterAmountInputPresenter {
                    override val sliderValue = 10f
                    override val amount = "10"
                },
            ) {
                Counter()
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun CounterLoadingPreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterPresenter provides object : CounterPresenter {
                    override val count = "0"
                    override val countLabel = "Zero"
                    override val isLoading = true
                }
            ) {
                Counter()
            }
        }
    }
}
