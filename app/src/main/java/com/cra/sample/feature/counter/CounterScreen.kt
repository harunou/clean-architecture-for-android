package com.cra.sample.feature.counter

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cra.sample.feature.counter.counter.Counter
import com.cra.sample.ui.theme.CounterTheme

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    Counter(
        correction = 0,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun CounterScreenPreview() {
    CounterTheme {
        Surface {
            CounterScreen()
        }
    }
}
