package com.amrubio27.cursotestingandroid.core.builder

import com.amrubio27.cursotestingandroid.cart.data.local.database.entity.CartItemEntity


class CartItemEntityBuilder {
    private var productId: String = "cart-item-1"
    private var quantity: Int = 1

    fun withProductId(productId: String) = apply { this.productId = productId }
    fun withQuantity(quantity: Int) = apply { this.quantity = quantity }

    fun build() = CartItemEntity(productId, quantity)
}

fun cartItemEntity(block: CartItemEntityBuilder.() -> Unit = {}) =
    CartItemEntityBuilder().apply(block).build()
