package com.amrubio27.cursotestingandroid.checkout.domain.repository

import com.amrubio27.cursotestingandroid.checkout.domain.model.OrderConfirmation

interface OrderRepository {
    suspend fun placeOrder(): OrderConfirmation
}
