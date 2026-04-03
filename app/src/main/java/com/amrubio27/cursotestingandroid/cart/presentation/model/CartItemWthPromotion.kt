package com.amrubio27.cursotestingandroid.cart.presentation.model

import com.amrubio27.cursotestingandroid.cart.domain.model.CartItem
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion

data class CartItemWithPromotion(
    val cartItem: CartItem,
    val item: ProductWithPromotion
)