package com.example.counter.counter

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.counter.amountinput.AmountInput
import com.example.counter.ui.theme.CounterTheme

@Composable
fun CounterUi(
    count: String,
    countLabel: String,
    isProgressIndicatorVisible: Boolean,
    onIncrementClick: () -> Unit,
    onDecrementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                text = countLabel,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onDecrementClick) { Text("-") }
                Button(onClick = onIncrementClick) { Text("+") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AmountInput()
        }
        if (isProgressIndicatorVisible) {
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
private fun CounterZeroPreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "0", countLabel = "Zero", isProgressIndicatorVisible = false, onIncrementClick = {}, onDecrementClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Positive")
@Composable
private fun CounterPositivePreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "5", countLabel = "Positive", isProgressIndicatorVisible = false, onIncrementClick = {}, onDecrementClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Negative")
@Composable
private fun CounterNegativePreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "-3", countLabel = "Negative", isProgressIndicatorVisible = false, onIncrementClick = {}, onDecrementClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun CounterLoadingPreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "0", countLabel = "Zero", isProgressIndicatorVisible = true, onIncrementClick = {}, onDecrementClick = {})
        }
    }
}
