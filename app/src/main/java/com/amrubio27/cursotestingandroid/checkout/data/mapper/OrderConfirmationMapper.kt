package com.amrubio27.cursotestingandroid.checkout.data.mapper

import com.amrubio27.cursotestingandroid.checkout.data.remote.response.OrderConfirmationResponse
import com.amrubio27.cursotestingandroid.checkout.domain.model.OrderConfirmation

fun OrderConfirmationResponse.toDomain(): OrderConfirmation {
    return OrderConfirmation(
        orderId = orderId,
        etaMinutes = etaMinutes,
        total = total
    )
}
