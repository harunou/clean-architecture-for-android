package com.example.counter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.counter.ui.theme.CounterTheme

interface CounterAmountInputPresenter {
    val sliderValue: Float
    val amount: String
}

object CounterAmountInputPresenterMock : CounterAmountInputPresenter {
    override val sliderValue = 1f
    override val amount = "1"
}

val LocalCounterAmountInputPresenter = compositionLocalOf<CounterAmountInputPresenter> {
    CounterAmountInputPresenterMock
}

interface CounterAmountInputController {
    fun onSliderValueChange(value: Float)
}

object CounterAmountInputControllerMock : CounterAmountInputController {
    override fun onSliderValueChange(value: Float) {}
}

val LocalCounterAmountInputController = compositionLocalOf<CounterAmountInputController> {
    CounterAmountInputControllerMock
}

@Composable
fun makeCounterAmountInputPresenter(): CounterAmountInputPresenter {
    if (LocalInspectionMode.current) return LocalCounterAmountInputPresenter.current

    val sliderValueFlow = LocalSliderValue.current
    val sliderValue = sliderValueFlow.collectAsStateWithLifecycle()

    return remember {
        object : CounterAmountInputPresenter {
            override val sliderValue: Float get() = sliderValue.value
            override val amount: String get() = sliderValue.value.toInt().toString()
        }
    }
}

@Composable
fun makeCounterAmountInputController(): CounterAmountInputController {
    if (LocalInspectionMode.current) return LocalCounterAmountInputController.current

    val sliderValueFlow = LocalSliderValue.current

    return remember {
        object : CounterAmountInputController {
            override fun onSliderValueChange(value: Float) { sliderValueFlow.value = value }
        }
    }
}

@Composable
fun CounterAmountInput(modifier: Modifier = Modifier) {
    val presenter = makeCounterAmountInputPresenter()
    val controller = makeCounterAmountInputController()
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Amount: ${presenter.amount}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Slider(
            value = presenter.sliderValue,
            onValueChange = controller::onSliderValueChange,
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterAmountInputPreview() {
    CounterTheme {
        Surface {
            CounterAmountInput()
        }
    }
}
