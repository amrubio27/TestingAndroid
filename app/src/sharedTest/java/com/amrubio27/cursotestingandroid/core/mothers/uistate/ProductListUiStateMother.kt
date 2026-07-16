package com.amrubio27.cursotestingandroid.core.mothers.uistate

import com.amrubio27.cursotestingandroid.core.mothers.ProductMother
import com.amrubio27.cursotestingandroid.core.mothers.PromotionMother
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import com.amrubio27.cursotestingandroid.productlist.presentation.ProductListUiState

object ProductListUiStateMother {
    fun success(
        products: List<ProductWithPromotion> =
            listOf(
                ProductWithPromotion(ProductMother.coffee(), PromotionMother.percent()),
                ProductWithPromotion(ProductMother.bread()),
                ProductWithPromotion(ProductMother.milk()),
            /*ProductWithPromotion(product {withId("123")}),
            ProductWithPromotion(product {withId("1234")}),
            ProductWithPromotion(product {withId("12345")}),
            ProductWithPromotion(product {withId("1234556")}),*/
            ),
        categories: List<String> = listOf("bread", "drinks", "lacteo"),
        selectedCategory: String? = null,
        sortOption: SortOption = SortOption.NONE,
    ) = ProductListUiState.Success(products, categories, selectedCategory, sortOption)
}