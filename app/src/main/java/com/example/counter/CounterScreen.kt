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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.compose.ui.platform.LocalInspectionMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.counter.ui.theme.CounterTheme

@HiltViewModel
class CounterViewModel @Inject constructor(
    val repository: CounterRepository,
) : ViewModel()

enum class CounterLabel { POSITIVE, NEGATIVE, ZERO }

interface CounterScreenPresenter {
    val count: String
    val countLabel: String
    val isLoading: Boolean
}

interface CounterScreenController {
    fun onLaunch()
    fun onPlusButtonClick()
    fun onMinusButtonClick()
}

object CounterScreenPresenterMock : CounterScreenPresenter {
    override val count = "0"
    override val countLabel = "Zero"
    override val isLoading = false
}

object CounterScreenControllerMock : CounterScreenController {
    override fun onLaunch() {}
    override fun onPlusButtonClick() {}
    override fun onMinusButtonClick() {}
}

val LocalCounterScreenPresenter = compositionLocalOf<CounterScreenPresenter> {
    CounterScreenPresenterMock
}

val LocalCounterScreenController = compositionLocalOf<CounterScreenController> {
    CounterScreenControllerMock
}

@Composable
fun makeCounterScreenPresenter(isLoadingFlow: StateFlow<Boolean>): CounterScreenPresenter {
    if (LocalInspectionMode.current) return LocalCounterScreenPresenter.current

    val vm = hiltViewModel<CounterViewModel>()
    val scope = rememberCoroutineScope()

    val counter = remember(vm) {
        vm.repository.observe()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Counter(),
            )
    }.collectAsStateWithLifecycle()

    val isLoading = isLoadingFlow.collectAsStateWithLifecycle()

    return remember(vm) {
        object : CounterScreenPresenter {
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
fun makeCounterScreenController(isLoadingFlow: MutableStateFlow<Boolean>): CounterScreenController {
    if (LocalInspectionMode.current) return LocalCounterScreenController.current

    val vm = hiltViewModel<CounterViewModel>()
    val scope = rememberCoroutineScope()

    return remember(vm) {
        object : CounterScreenController {
            override fun onLaunch() {
                scope.launch {
                    isLoadingFlow.value = true
                    vm.repository.load()
                    isLoadingFlow.value = false
                }
            }
            override fun onPlusButtonClick() {
                scope.launch {
                    isLoadingFlow.value = true
                    vm.repository.increment()
                    isLoadingFlow.value = false
                }
            }
            override fun onMinusButtonClick() {
                scope.launch {
                    isLoadingFlow.value = true
                    vm.repository.decrement()
                    isLoadingFlow.value = false
                }
            }
        }
    }
}

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    val isLoading = remember { MutableStateFlow(false) }
    val presenter = makeCounterScreenPresenter(isLoading)
    val controller = makeCounterScreenController(isLoading)

    LaunchedEffect(controller) { controller.onLaunch() }

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

@Preview(showBackground = true, name = "Zero")
@Composable
private fun CounterScreenZeroPreview() {
    CounterTheme {
        Surface {
            CounterScreen()
        }
    }
}

@Preview(showBackground = true, name = "Positive")
@Composable
private fun CounterScreenPositivePreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterScreenPresenter provides object : CounterScreenPresenter {
                    override val count = "5"
                    override val countLabel = "Positive"
                    override val isLoading = false
                }
            ) {
                CounterScreen()
            }
        }
    }
}

@Preview(showBackground = true, name = "Negative")
@Composable
private fun CounterScreenNegativePreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterScreenPresenter provides object : CounterScreenPresenter {
                    override val count = "-3"
                    override val countLabel = "Negative"
                    override val isLoading = false
                }
            ) {
                CounterScreen()
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun CounterScreenLoadingPreview() {
    CounterTheme {
        Surface {
            CompositionLocalProvider(
                LocalCounterScreenPresenter provides object : CounterScreenPresenter {
                    override val count = "0"
                    override val countLabel = "Zero"
                    override val isLoading = true
                }
            ) {
                CounterScreen()
            }
        }
    }
}
