package com.amrubio27.cursotestingandroid.productlist.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.amrubio27.cursotestingandroid.core.mothers.uistate.ProductListUiStateMother
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import org.junit.Rule

class ProductListScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createProductListScreen(
        uiState: ProductListUiState = ProductListUiStateMother.success(),
        cartItemCount: Int = 0,
        filterVisible: Boolean = true,
        onFilterSelected: (Boolean) -> Unit = {},
        onSettingsSelected: () -> Unit = {},
        onCartSelected: () -> Unit = {},
        onCategorySelected: (String?) -> Unit = {},
        onSortOptionSelected: (SortOption) -> Unit = {},
        onProductSelected: (ProductWithPromotion) -> Unit = {},
    ) {
        composeRule.setContent {
            ProductListContent(
                uiState = uiState,
                cartItemCount = cartItemCount,
                filtersVisible = filterVisible,
                onFilterSelected = onFilterSelected,
                onSettingsSelected = onSettingsSelected,
                onCartSelected = onCartSelected,
                onCategorySelected = onCategorySelected,
                onSortOptionSelected = onSortOptionSelected,
                onProductSelected = onProductSelected
            )
        }
    }
}