package com.amrubio27.cursotestingandroid.productlist.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.bread
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.coffee
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.milk
import com.amrubio27.cursotestingandroid.core.mothers.uistate.ProductListUiStateMother
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.FILTER_VIEW
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.PRODUCT_LIST_LOADING
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListItem
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import org.junit.Rule
import kotlin.test.Test

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

    @Test
    fun givenLoadingState_whenRendered_thenShowsProgressView() {
        createProductListScreen(uiState = ProductListUiState.Loading)

        composeRule.onNodeWithTag(testTag = PRODUCT_LIST_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowsErrorMessage() {
        createProductListScreen(uiState = ProductListUiState.Error(""))

        composeRule.onNodeWithText("ERROR").assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsProductsAndCount() {
        createProductListScreen(uiState = ProductListUiStateMother.success())

        composeRule.onNodeWithText("3 productos").assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = FILTER_VIEW).assertIsDisplayed()

        composeRule.onNodeWithTag(testTag = productListItem(productId = coffee().id))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = productListItem(productId = bread().id))
            .assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = productListItem(productId = milk().id))
            .assertIsDisplayed()

        //Como apunte si necesitamos hacer scroll por que no se vean los items
        //composeRule.onNodeWithTag(testTag = PRODUCT_LIST_LIST).performScrollToIndex
        //composeRule.onNodeWithTag(testTag = PRODUCT_LIST_LIST).performScrollToNode(hasTestTag(productListItem("1234556")))

        //composeRule.onNodeWithTag(testTag = productListItem("1234556")).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsEmptyMessage() {
        createProductListScreen(uiState = ProductListUiStateMother.success(products = emptyList()))

        composeRule.onNodeWithText("No se encontraron productos").assertIsDisplayed()
    }

}