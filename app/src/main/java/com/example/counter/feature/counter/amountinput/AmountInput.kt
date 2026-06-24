package com.example.counter.feature.counter.amountinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

// --- DOMAIN ---

@ViewModelScoped
class Store @Inject constructor() {
    private val _sliderValue = MutableStateFlow(1f)
    val sliderValue: StateFlow<Float> = _sliderValue.asStateFlow()

    fun setSliderValue(value: Float) {
        _sliderValue.value = value
    }
}

// --- COMPONENT ---

@HiltViewModel
class AmountInputViewModel @Inject constructor(
    private val store: Store,
) : ViewModel() {
    val sliderValue: StateFlow<Float> = store.sliderValue
    val labelAmount: StateFlow<Int> = store.sliderValue
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    fun onSliderValueChange(value: Float) = store.setSliderValue(value)
}

// Public entry point of the component
// Wires AmountInputViewModel to AmountInputUi.
@Composable
fun AmountInput(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        AmountInputUi(sliderValue = 1f, labelAmount = 1, onSliderValueChange = {}, modifier = modifier)
        return
    }

    val viewModel: AmountInputViewModel = hiltViewModel(
        viewModelStoreOwner = rememberViewModelStoreOwner(),
    )
    val sliderValue by viewModel.sliderValue.collectAsStateWithLifecycle()
    val labelAmount by viewModel.labelAmount.collectAsStateWithLifecycle()

    AmountInputUi(
        sliderValue = sliderValue,
        labelAmount = labelAmount,
        onSliderValueChange = viewModel::onSliderValueChange,
        modifier = modifier,
    )
}
