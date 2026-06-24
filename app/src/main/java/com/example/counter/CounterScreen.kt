package com.example.counter

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.counter.counter.Counter

@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    Counter(
        correction = 0,
        modifier = modifier,
    )
}
