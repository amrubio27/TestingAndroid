package com.amrubio27.cursotestingandroid.checkout.presentation

sealed interface CheckOutEvent {
    data class ShowMessage(val message: String) : CheckOutEvent
}
