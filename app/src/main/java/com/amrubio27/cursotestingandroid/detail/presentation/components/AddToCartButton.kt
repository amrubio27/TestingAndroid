package com.amrubio27.cursotestingandroid.detail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product

@Composable
fun AddToCartButton(
    modifier: Modifier = Modifier,
    product: Product?,
    isLoading: Boolean,
    addToCart: () -> Unit
) {

    product?.let {
        if (it.stock > 0) {
            AddToCartButtonWithStock(modifier, it, isLoading, addToCart)
        } else {
            AddToCartButtonNoStock(modifier)
        }
    }

}