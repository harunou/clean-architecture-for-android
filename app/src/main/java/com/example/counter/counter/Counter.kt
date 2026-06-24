package com.example.counter.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.compose.ui.platform.LocalInspectionMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

// --- COMPONENT ---

@HiltViewModel
class CounterViewModel @Inject constructor() : ViewModel() {
    private val _count = MutableStateFlow(0)

    val count: StateFlow<String> = _count
        .map { it.toString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "0")

    val countLabel: StateFlow<String> = _count
        .map { value ->
            when {
                value > 0 -> "Positive"
                value < 0 -> "Negative"
                else -> "Zero"
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Zero")

    val isProgressIndicatorVisible: StateFlow<Boolean> = MutableStateFlow(false)

    fun onIncrementClick() { _count.value++ }
    fun onDecrementClick() { _count.value-- }
}

// Public entry point of the component; wires CounterViewModel to CounterUi.
@Composable
fun Counter(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        CounterUi(count = "0", countLabel = "Zero", isProgressIndicatorVisible = false, onIncrementClick = {}, onDecrementClick = {}, modifier = modifier)
        return
    }

    val viewModel: CounterViewModel = hiltViewModel(
        viewModelStoreOwner = rememberViewModelStoreOwner(),
    )
    val count by viewModel.count.collectAsStateWithLifecycle()
    val countLabel by viewModel.countLabel.collectAsStateWithLifecycle()
    val isProgressIndicatorVisible by viewModel.isProgressIndicatorVisible.collectAsStateWithLifecycle()

    CounterUi(
        count = count,
        countLabel = countLabel,
        isProgressIndicatorVisible = isProgressIndicatorVisible,
        onIncrementClick = viewModel::onIncrementClick,
        onDecrementClick = viewModel::onDecrementClick,
        modifier = modifier,
    )
}
