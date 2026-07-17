package com.amrubio27.cursotestingandroid.core.mothers.uistate

import com.amrubio27.cursotestingandroid.core.mothers.ProductMother
import com.amrubio27.cursotestingandroid.detail.presentation.ProductDetailUiState
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion

object ProductDetailUiStateMother {
    fun success(
        product: Product = ProductMother.coffee(),
        promotion: ProductPromotion? = null,
        isLoading: Boolean = false,
    ) = ProductDetailUiState(
        item = ProductWithPromotion(product, promotion),
        isLoading = isLoading,
    )

    fun loading() =
        ProductDetailUiState(
            item = null,
            isLoading = true,
        )
}
