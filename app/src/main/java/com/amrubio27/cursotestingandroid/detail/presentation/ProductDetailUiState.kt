package com.amrubio27.cursotestingandroid.detail.presentation

import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion

data class ProductDetailUiState(
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true
)