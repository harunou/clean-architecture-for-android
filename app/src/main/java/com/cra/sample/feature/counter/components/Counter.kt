package com.cra.sample.feature.counter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.cra.sample.core.Input
import com.cra.sample.core.ProvideInput
import com.cra.sample.feature.counter.CounterStore
import com.cra.sample.feature.counter.repository.CounterRepository
import com.cra.sample.ui.theme.CounterTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- COMPONENT ---
//
// A "component" is a self-contained UI unit built from three parts that live together
// in this package:
//   1. Counter          - the component binder (the public entry point). It is the
//                          @Composable that connects the component class to its component
//                          template. Its code is structural boilerplate - a candidate for
//                          compile-time code generation.
//   2. CounterViewModel - the component class. It manages the component's state and
//                          handles user interactions by coordinating between the component
//                          template and its collaborators. It contains UI-related logic and
//                          delegates business logic, data access, and other non-UI
//                          responsibilities to collaborators such as stores, repositories,
//                          usecases, etc.
//   3. CounterUi        - the component template. It binds to the component class's props to
//                          display data, and captures user interactions to invoke methods on
//                          the component class. It accepts only primitive values and plain
//                          data objects.
//
// The same component expressed in an Angular-style framework would look roughly like:
//
//   // Component binder
//   @Component({
//     selector: 'counter',
//     // Component template
//     template: `
//       <h1>{{ count }}</h1>
//       <h2>{{ countLabel }}</h2>
//       <button (click)="onDecrementClick()">-</button>
//       <button (click)="onIncrementClick()">+</button>
//       <amount-input></amount-input>
//     `,
//   })
//   // Component class
//   export class CounterComponent {
//     // injection analogue: collaborators resolved from the DI container (not implemented here)
//     private repository = inject(CounterRepository);
//     private store = inject(CounterStore);
//
//     // signal input + computed signals - the component's reactive state (kept private)
//     correction = input(0);
//     private _count = computed(() => `${this.repository.counter() + this.correction()}`);
//     private _countLabel = computed(() => {
//       const total = this.repository.counter() + this.correction();
//       return total > 0 ? 'Positive' : total < 0 ? 'Negative' : 'Zero';
//     });
//
//     // Presenter
//     // getters expose plain primitives to the template; the signals stay private
//     get count(): string { return this._count(); }
//     get countLabel(): string { return this._countLabel(); }
//
//     // Controller
//     // behavior - delegates to the injected collaborators
//     onIncrementClick() { this.repository.increment(this.store.incrementAmount()); }
//     onDecrementClick() { this.repository.decrement(this.store.incrementAmount()); }
//   }
//

@Composable
fun Counter(correction: Int, modifier: Modifier = Modifier) {
    // This could be resoloved with DI to have preview in integration
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
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "0")

    val countLabel: StateFlow<String> = combine(counterRepository.counter, correction.flow) { entity, correction ->
        val total = entity.value + correction
        when {
            total > 0 -> "Positive"
            total < 0 -> "Negative"
            else -> "Zero"
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "Zero")

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

@Composable
fun CounterUi(
    count: String,
    countLabel: String,
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
    }
}

@Preview(showBackground = true, name = "Zero")
@Composable
private fun CounterZeroPreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "0", countLabel = "Zero", onIncrementClick = {
            }, onDecrementClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Positive")
@Composable
private fun CounterPositivePreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "5", countLabel = "Positive", onIncrementClick = {
            }, onDecrementClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Negative")
@Composable
private fun CounterNegativePreview() {
    CounterTheme {
        Surface {
            CounterUi(count = "-3", countLabel = "Negative", onIncrementClick = {
            }, onDecrementClick = {})
        }
    }
}
