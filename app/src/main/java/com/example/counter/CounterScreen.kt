package com.example.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

enum class CounterLabel { POSITIVE, NEGATIVE, ZERO }

data class CounterUiState(
    val count: Int = 0,
    val label: CounterLabel = CounterLabel.ZERO,
)

interface CounterScreenPresenter {
    val count: String
    val countLabel: String
}

interface CounterScreenController {
    fun onPlusButtonClick()
    fun onMinusButtonClick()
}

object CounterScreenPresenterMock : CounterScreenPresenter {
    override val count = "0"
    override val countLabel = "Zero"
}

object CounterScreenControllerMock : CounterScreenController {
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
fun makeCounterScreenPresenter(viewModel: CounterViewModel): CounterScreenPresenter {
    if (LocalInspectionMode.current) return LocalCounterScreenPresenter.current

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    return remember(viewModel) {
        object : CounterScreenPresenter {
            override val count: String get() = uiState.value.count.toString()
            override val countLabel: String get() = uiState.value.label.name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

@Composable
fun makeCounterScreenController(viewModel: CounterViewModel): CounterScreenController {
    if (LocalInspectionMode.current) return LocalCounterScreenController.current

    return remember(viewModel) {
        object : CounterScreenController {
            override fun onPlusButtonClick() = viewModel.increment()
            override fun onMinusButtonClick() = viewModel.decrement()
        }
    }
}

@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    modifier: Modifier = Modifier,
) {
    val presenter = makeCounterScreenPresenter(viewModel)
    val controller = makeCounterScreenController(viewModel)
    Column(
        modifier = modifier.fillMaxSize(),
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
}
