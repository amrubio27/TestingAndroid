package com.amrubio27.cursotestingandroid.productlist.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.amrubio27.cursotestingandroid.R
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.bread
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.coffee
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.milk
import com.amrubio27.cursotestingandroid.core.mothers.uistate.ProductListUiStateMother
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.FILTER_VIEW
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.PRODUCT_LIST_LOADING
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.TOP_APP_BAR_BADGE
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.TOP_APP_BAR_CART
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.TOP_APP_BAR_FILTER
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.TOP_APP_BAR_SETTINGS
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListCategory
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListItem
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListSortOption
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
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

    private fun getString(resId: Int): String = composeRule.activity.getString(resId)
    private fun getString(resId: Int, vararg formatArgs: Any): String =
        composeRule.activity.getString(resId, *formatArgs)

    @Test
    fun givenLoadingState_whenRendered_thenShowsProgressView() {
        createProductListScreen(uiState = ProductListUiState.Loading)

        composeRule.onNodeWithTag(testTag = PRODUCT_LIST_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowsErrorMessage() {
        createProductListScreen(uiState = ProductListUiState.Error(""))

        composeRule.onNodeWithText(getString(R.string.error_title)).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsProductsAndCount() {
        createProductListScreen(uiState = ProductListUiStateMother.success())

        composeRule.onNodeWithText(getString(R.string.product_list_count, 3)).assertIsDisplayed()
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

        composeRule.onNodeWithText(getString(R.string.product_list_no_products)).assertIsDisplayed()
    }

    @Test
    fun givenNoCategorySelected_whenRendered_thenMarkAllChip() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(selectedCategory = null)
        )

        composeRule.onNodeWithTag(testTag = productListCategory(null)).assertIsSelected()
    }

    @Test
    fun givenCategorySelected_whenRendered_thenMarkThatChip() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(selectedCategory = "drinks")
        )

        composeRule.onNodeWithTag(testTag = productListCategory("drinks")).assertIsSelected()
    }

    @Test
    fun givenPriceAscSelected_whenRendered_thenMarkThatChip() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(sortOption = SortOption.PRICE_ASC)
        )

        composeRule.onNodeWithTag(testTag = productListSortOption(SortOption.PRICE_ASC))
            .assertIsSelected()
    }

    @Test
    fun givenPriceDescSelected_whenRendered_thenMarkThatChip() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(sortOption = SortOption.PRICE_DESC)
        )

        composeRule.onNodeWithTag(testTag = productListSortOption(SortOption.PRICE_DESC))
            .assertIsSelected()
    }

    @Test
    fun givenDiscountSelected_whenRendered_thenMarkThatChip() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(sortOption = SortOption.DISCOUNT)
        )

        composeRule.onNodeWithTag(testTag = productListSortOption(SortOption.DISCOUNT))
            .assertIsSelected()
    }

    @Test
    fun givenCartItemCountZero_whenRendered_thenHidesBadge() {
        createProductListScreen(cartItemCount = 0)

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_BADGE).assertDoesNotExist()
    }

    @Test
    fun givenCartItemCountPositive_whenRendered_thenShowsBadgeWithCount() {
        createProductListScreen(cartItemCount = 67)

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_BADGE).assertIsDisplayed()
        composeRule.onNodeWithText("67").assertIsDisplayed()
    }

    @Test
    fun givenCartItemCountOver99_whenRendered_thenShows99Plus() {
        createProductListScreen(cartItemCount = 345)

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_BADGE).assertIsDisplayed()
        composeRule.onNodeWithText("99+").assertIsDisplayed()
    }

    @Test
    fun givenFiltersVisible_whenToggleClicked_thenEmitFalse() {
        var emitted: Boolean? = null
        createProductListScreen(
            filterVisible = true,
            onFilterSelected = { emitted = it })

        composeRule.onNodeWithTag(TOP_APP_BAR_FILTER).performClick()
        assertEquals(false, emitted)
    }

    @Test
    fun givenFiltersHidden_whenToggleClicked_thenEmitTrue() {
        var emitted: Boolean? = null
        createProductListScreen(
            filterVisible = false,
            onFilterSelected = { emitted = it })

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_FILTER).performClick()
        assertEquals(true, emitted)
    }

    @Test
    fun givenProductListRendered_whenSettingsIconClicked_thenEmitCallback() {
        var settingClicked = false
        createProductListScreen(onSettingsSelected = { settingClicked = true })

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_SETTINGS).performClick()
        assertTrue(settingClicked)
    }

    @Test
    fun givenProductListRendered_whenCartIconClicked_thenEmitCallback() {
        var cartClicked = false
        createProductListScreen(onCartSelected = { cartClicked = true })

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR_CART).performClick()
        assertTrue(cartClicked)
    }

    @Test
    fun givenProductListRendered_whenSortDiscountClick_thenEmitsSortDiscountOption() {
        var selectedSort: SortOption? = null
        createProductListScreen(onSortOptionSelected = { newSort -> selectedSort = newSort })

        composeRule.onNodeWithTag(testTag = productListSortOption(sortOption = SortOption.DISCOUNT))
            .performClick()

        assertEquals(SortOption.DISCOUNT, selectedSort)
    }

    @Test
    fun givenProductListRendered_whenSortPriceDescClick_thenEmitsSortPriceDescOption() {
        var selectedSort: SortOption? = null
        createProductListScreen(onSortOptionSelected = { newSort -> selectedSort = newSort })

        composeRule.onNodeWithTag(testTag = productListSortOption(sortOption = SortOption.PRICE_DESC))
            .performClick()

        assertEquals(SortOption.PRICE_DESC, selectedSort)
    }

    @Test
    fun givenProductListRendered_whenSortPriceAscClick_thenEmitsSortPriceAscOption() {
        var selectedSort: SortOption? = null
        createProductListScreen(onSortOptionSelected = { newSort -> selectedSort = newSort })

        composeRule.onNodeWithTag(testTag = productListSortOption(sortOption = SortOption.PRICE_ASC))
            .performClick()

        assertEquals(SortOption.PRICE_ASC, selectedSort)
    }

}