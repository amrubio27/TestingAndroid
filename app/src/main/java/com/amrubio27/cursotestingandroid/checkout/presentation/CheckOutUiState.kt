package com.amrubio27.cursotestingandroid.checkout.presentation

import com.amrubio27.cursotestingandroid.cart.domain.model.CartSummary
import com.amrubio27.cursotestingandroid.checkout.domain.model.OrderConfirmation
import com.amrubio27.cursotestingandroid.checkout.presentation.model.CheckOutForm
import com.amrubio27.cursotestingandroid.checkout.presentation.model.CheckoutFormErrors

sealed class CheckOutUiState {
    data object Loading : CheckOutUiState()
    data class Success(val confirmation: OrderConfirmation) : CheckOutUiState()
    data class Error(val message: String) : CheckOutUiState()
    data class Idle(
        val summary: CartSummary,
        val form: CheckOutForm,
        val errors: CheckoutFormErrors,
        val isCartEmpty: Boolean,
        val isSubmitting: Boolean,
        val canSubmit: Boolean
    ) : CheckOutUiState()
}
