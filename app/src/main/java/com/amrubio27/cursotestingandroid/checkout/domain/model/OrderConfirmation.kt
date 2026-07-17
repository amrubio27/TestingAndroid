package com.amrubio27.cursotestingandroid.checkout.domain.model

data class OrderConfirmation(
    val orderId: String,
    val etaMinutes: Int,
    val total: Double
)
