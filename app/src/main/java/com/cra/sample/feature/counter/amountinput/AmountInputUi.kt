package com.cra.sample.feature.counter.amountinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cra.sample.ui.theme.CounterTheme

@Composable
fun AmountInputUi(
    sliderValue: Float,
    labelAmount: Int,
    onSliderValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Amount: $labelAmount",
            style = MaterialTheme.typography.bodyMedium,
        )
        Slider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        )
    }
}

@Preview(showBackground = true, name = "Min")
@Composable
private fun AmountInputMinPreview() {
    CounterTheme {
        Surface {
            AmountInputUi(sliderValue = 1f, labelAmount = 1, onSliderValueChange = {})
        }
    }
}

@Preview(showBackground = true, name = "Mid")
@Composable
private fun AmountInputMidPreview() {
    CounterTheme {
        Surface {
            AmountInputUi(sliderValue = 5f, labelAmount = 5, onSliderValueChange = {})
        }
    }
}

@Preview(showBackground = true, name = "Max")
@Composable
private fun AmountInputMaxPreview() {
    CounterTheme {
        Surface {
            AmountInputUi(sliderValue = 10f, labelAmount = 10, onSliderValueChange = {})
        }
    }
}
