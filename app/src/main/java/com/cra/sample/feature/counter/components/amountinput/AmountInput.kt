package com.cra.sample.feature.counter.components.amountinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.cra.sample.feature.counter.CounterStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// --- COMPONENT ---
//
// A "component" is a self-contained UI unit built from three parts that live together
// in this package:
//   1. AmountInput          - the component binder (the public entry point). It is the
//                             @Composable that connects the component class to its component
//                             template. Its code is structural boilerplate - a candidate for
//                             compile-time code generation.
//   2. AmountInputViewModel - the component class. It manages the component's state and
//                             handles user interactions by coordinating between the
//                             component template and its collaborators. It contains
//                             UI-related logic and delegates business logic, data access,
//                             and other non-UI responsibilities to collaborators such as
//                             stores, repositories, usecases, etc.
//   3. AmountInputUi        - the component template. It binds to the component class's
//                             props to display data, and captures user interactions to
//                             invoke methods on the component class. It accepts only
//                             primitive values and plain data objects.

@Composable
fun AmountInput(modifier: Modifier = Modifier) {
    if (LocalInspectionMode.current) {
        AmountInputUi(sliderValue = 1f, labelAmount = 1, onSliderValueChange = {
        }, modifier = modifier)
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

@HiltViewModel
class AmountInputViewModel @Inject constructor(private val store: CounterStore) : ViewModel() {
    // Presenter
    val sliderValue: StateFlow<Float> = store.incrementAmount
    val labelAmount: StateFlow<Int> = store.incrementAmount
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Controller
    fun onSliderValueChange(value: Float) = store.setIncrementAmount(value)
}
