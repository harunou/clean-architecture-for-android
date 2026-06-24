package com.cra.sample.feature.counter.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.cra.sample.core.Input
import com.cra.sample.core.ProvideInput
import com.cra.sample.feature.counter.CounterStore
import com.cra.sample.feature.counter.repository.CounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- COMPONENT ---

// Public entry point of the component; wires CounterViewModel to CounterUi.
// This is structural boilerplate - a candidate for compile-time code generation.
@Composable
fun Counter(correction: Int, modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        CounterUi(count = "0", countLabel = "Zero", onIncrementClick = {}, onDecrementClick = {}, modifier = modifier)
        return
    }

    val viewModel: CounterViewModel = hiltViewModel(
        viewModelStoreOwner = rememberViewModelStoreOwner(),
    )

    ProvideInput(viewModel.correction, correction)

    val count by viewModel.count.collectAsStateWithLifecycle()
    val countLabel by viewModel.countLabel.collectAsStateWithLifecycle()

    // This should have strict mapping to the public properties of the view model
    CounterUi(
        count = count,
        countLabel = countLabel,
        onIncrementClick = viewModel::onIncrementClick,
        onDecrementClick = viewModel::onDecrementClick,
        modifier = modifier,
    )
}

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val counterRepository: CounterRepository,
    private val counterStore: CounterStore,
) : ViewModel() {
    val correction = Input(0)

    // Presenter
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

    // Controller
    fun onIncrementClick() {
        viewModelScope.launch {
            counterRepository.increment(counterStore.incrementAmount.value.toInt())
        }
    }

    fun onDecrementClick() {
        viewModelScope.launch {
            counterRepository.decrement(counterStore.incrementAmount.value.toInt())
        }
    }
}
