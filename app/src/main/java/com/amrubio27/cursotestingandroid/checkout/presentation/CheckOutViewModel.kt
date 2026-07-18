package com.amrubio27.cursotestingandroid.checkout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.cart.domain.usecase.GetCartSummaryUseCase
import com.amrubio27.cursotestingandroid.checkout.domain.usecase.PlaceOrderUseCase
import com.amrubio27.cursotestingandroid.checkout.presentation.model.CheckOutForm
import com.amrubio27.cursotestingandroid.checkout.presentation.model.validate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase,
    getCartSummaryUseCase: GetCartSummaryUseCase
) : ViewModel() {

    private val formState = MutableStateFlow(CheckOutForm())
    private val submission = MutableStateFlow<Submission>(Submission.Idle)

    private val _events = MutableSharedFlow<CheckOutEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<CheckOutEvent> = _events

    val uiState: StateFlow<CheckOutUiState> = combine(
        getCartSummaryUseCase(), formState, submission
    ) { summary, form, sub ->
        when (sub) {
            is Submission.Success -> CheckOutUiState.Success(sub.confirmation)
            is Submission.Failed -> CheckOutUiState.Error(sub.message)
            Submission.Idle, Submission.Submitting -> {
                val errors = form.validate()
                val isCartEmpty = summary.subtotal <= 0.0
                val isSubmitting = sub == Submission.Submitting
                CheckOutUiState.Idle(
                    summary = summary,
                    form = form,
                    errors = errors,
                    isCartEmpty = isCartEmpty,
                    isSubmitting = isSubmitting,
                    canSubmit = !isCartEmpty && !isSubmitting && errors.isValid
                )
            }
        }

    }.catch { e ->
        _events.emit(CheckOutEvent.ShowMessage(e.message.orEmpty()))
        emit(CheckOutUiState.Error(e.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckOutUiState.Loading
    )

}
