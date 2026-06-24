package com.example.counter.feature.counter.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.example.counter.core.Input
import com.example.counter.core.ProvideInput
import com.example.counter.feature.counter.AmountStore
import com.example.counter.feature.counter.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- COMPONENT ---

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val counterRepository: CounterRepository,
    private val amountStore: AmountStore,
) : ViewModel() {
    val correction = Input(0)

    val count: StateFlow<String> = combine(counterRepository.counter, correction.flow) { entity, correction ->
        (entity.value + correction).toString()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "0")

    val countLabel: StateFlow<String> = combine(counterRepository.counter, correction.flow) { entity, correction ->
        val total = entity.value + correction
        when {
            total > 0 -> "Positive"
            total < 0 -> "Negative"
            else -> "Zero"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Zero")

    val isProgressIndicatorVisible: StateFlow<Boolean> = counterRepository.isLoading
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onIncrementClick() {
        viewModelScope.launch {
            counterRepository.increment(amountStore.sliderValue.value.toInt())
        }
    }

    fun onDecrementClick() {
        viewModelScope.launch {
            counterRepository.decrement(amountStore.sliderValue.value.toInt())
        }
    }
}

// Public entry point of the component; wires AmountInputViewModel to AmountInputUi.
// This is structural boilerplate - a candidate for compile-time code generation.
@Composable
fun Counter(correction: Int, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        CounterUi(count = "0", countLabel = "Zero", isProgressIndicatorVisible = false, onIncrementClick = {
        }, onDecrementClick = {}, modifier = modifier)
        return
    }

    val viewModel: CounterViewModel = hiltViewModel(
        viewModelStoreOwner = rememberViewModelStoreOwner(),
    )

    ProvideInput(viewModel.correction, correction)

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
