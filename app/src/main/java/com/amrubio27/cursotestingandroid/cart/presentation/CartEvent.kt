package com.amrubio27.cursotestingandroid.cart.presentation

sealed interface CartEvent {
    data class ShowMessage(val message: String) : CartEvent
}