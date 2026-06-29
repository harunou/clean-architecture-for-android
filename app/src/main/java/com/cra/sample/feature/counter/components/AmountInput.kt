package com.cra.sample.feature.counter.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.cra.sample.feature.counter.CounterStore
import com.cra.sample.ui.theme.CounterTheme
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
//
// The same component expressed in an Angular-style framework would look roughly like:
//
//   @Component({
//     selector: 'amount-input',
//     template: `
//       <label>Amount: {{ labelAmount }}</label>
//       <input type="range" min="1" max="10" [value]="sliderValue"
//              (input)="onSliderValueChange($event)" />
//     `,
//   })
//   export class AmountInputComponent {
//     // injection analogue: collaborator resolved from the DI container (not implemented here)
//     private store = inject(CounterStore);
//
//     // computed signals - the component's reactive state (kept private)
//     private _sliderValue = computed(() => this.store.incrementAmount());
//     private _labelAmount = computed(() => Math.trunc(this.store.incrementAmount()));
//
//     // getters expose plain primitives to the template; the signals stay private
//     get sliderValue(): number { return this._sliderValue(); }
//     get labelAmount(): number { return this._labelAmount(); }
//
//     // behavior - reads the raw event and maps it before delegating to the collaborator
//     onSliderValueChange(event: Event) {
//       this.store.setIncrementAmount(+(event.target as HTMLInputElement).value);
//     }
//   }

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
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)
    val labelAmount: StateFlow<Int> = store.incrementAmount
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Controller
    fun onSliderValueChange(value: Float) = store.setIncrementAmount(value)
}

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
