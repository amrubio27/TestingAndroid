package com.amrubio27.cursotestingandroid.cart.domain.model

data class CartSummary(
    val subtotal: Double,
    val discountTotal: Double,
    val finalTotal: Double
)