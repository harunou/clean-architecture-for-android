package com.example.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.counter.ui.theme.CounterTheme

@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CounterContent(
        uiState = uiState,
        onIncrement = viewModel::increment,
        onDecrement = viewModel::decrement,
        modifier = modifier,
    )
}

@Composable
private fun CounterContent(
    uiState: CounterUiState,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = uiState.count.toString(),
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = uiState.label.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onDecrement) { Text("-") }
            Button(onClick = onIncrement) { Text("+") }
        }
    }
}

@Preview(showBackground = true, name = "Positive")
@Composable
private fun CounterPositivePreview() {
    CounterTheme {
        Surface {
            CounterContent(
                uiState = CounterUiState(count = 5, label = CounterLabel.POSITIVE),
                onIncrement = {},
                onDecrement = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Zero")
@Composable
private fun CounterZeroPreview() {
    CounterTheme {
        Surface {
            CounterContent(
                uiState = CounterUiState(count = 0, label = CounterLabel.ZERO),
                onIncrement = {},
                onDecrement = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Negative")
@Composable
private fun CounterNegativePreview() {
    CounterTheme {
        Surface {
            CounterContent(
                uiState = CounterUiState(count = -3, label = CounterLabel.NEGATIVE),
                onIncrement = {},
                onDecrement = {},
            )
        }
    }
}
