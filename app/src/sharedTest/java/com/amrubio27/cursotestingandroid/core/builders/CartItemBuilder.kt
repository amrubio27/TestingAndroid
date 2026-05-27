package com.amrubio27.cursotestingandroid.core.builders

import com.amrubio27.cursotestingandroid.cart.domain.model.CartItem

class CartItemBuilder {
    private var productId: String = "cart-item-1"
    private var quantity: Int = 1

    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build() = CartItem(productId, quantity)
}

fun cartItem(block: CartItemBuilder.() -> Unit = {}) = CartItemBuilder().apply(block).build()