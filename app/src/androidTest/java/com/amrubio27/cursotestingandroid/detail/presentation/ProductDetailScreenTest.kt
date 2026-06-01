package com.amrubio27.cursotestingandroid.detail.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.amrubio27.cursotestingandroid.R
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.coffee
import com.amrubio27.cursotestingandroid.core.mothers.PromotionMother.buyXPayY
import com.amrubio27.cursotestingandroid.core.mothers.PromotionMother.percent
import com.amrubio27.cursotestingandroid.core.mothers.uistate.ProductDetailUiStateMother
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.PRODUCT_DETAIL_ADD_TO_CART
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.PRODUCT_DETAIL_LOADING
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.TOP_APP_BAR
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createProductDetailScreen(
        uiState: ProductDetailUiState = ProductDetailUiStateMother.success(),
        onBack: () -> Unit = {},
        onAddToCart: () -> Unit = {}
    ) {
        composeRule.setContent {
            ProductDetailContent(
                uiState = uiState,
                onBack = onBack,
                onAddToCart = onAddToCart
            )
        }
    }

    private fun getString(resId: Int): String = composeRule.activity.getString(resId)
    private fun getString(resId: Int, vararg formatArgs: Any): String =
        composeRule.activity.getString(resId, *formatArgs)

    @Test
    fun givenLoadingState_whenRendered_thenShowsProgressView() {
        createProductDetailScreen(uiState = ProductDetailUiStateMother.loading())

        composeRule.onNodeWithTag(testTag = PRODUCT_DETAIL_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenSuccessStateNoPromotion_whenRendered_thenShowsProductDetailsAndAddToCartButtonEnabled() {
        val product = coffee(stock = 5)
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(
                product = product,
                promotion = null
            )
        )

        composeRule.onAllNodesWithText(product.name).onFirst().assertIsDisplayed()
        composeRule.onNodeWithText(product.category).assertIsDisplayed()
        composeRule.onNodeWithText(product.description).assertIsDisplayed()
        composeRule.onNodeWithText(product.price.toString()).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.detail_stock_units, 5)).assertIsDisplayed()

        composeRule.onNodeWithTag(testTag = PRODUCT_DETAIL_ADD_TO_CART).assertIsEnabled()
    }

    @Test
    fun givenSuccessStatePercentPromotion_whenRendered_thenShowsProductDiscountAndDiscountedPrice() {
        val product = coffee()
        val promo = percent(percent = 20.0, discountedPrice = 3.60)
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(
                product = product,
                promotion = promo
            )
        )

        // Precio original
        composeRule.onNodeWithText(product.price.toString()).assertIsDisplayed()
        // Descuento
        composeRule.onNodeWithText("3.6").assertIsDisplayed()
        // Porcentaje
        composeRule.onNodeWithText(getString(R.string.detail_percent_off, 20)).assertIsDisplayed()
    }

    @Test
    fun givenSuccessStateBuyXPayYPromotion_whenRendered_thenShowsBuyXPayYPromotionLabel() {
        val product = coffee()
        val promo = buyXPayY(buy = 3, pay = 2, label = "3x2")
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(
                product = product,
                promotion = promo
            )
        )

        // Precio original
        composeRule.onNodeWithText(product.price.toString()).assertIsDisplayed()
        // Etiqueta de promoción 3x2
        composeRule.onNodeWithText(getString(R.string.detail_promo, "3x2")).assertIsDisplayed()
    }

    @Test
    fun givenSuccessStateNoStock_whenRendered_thenShowsNoStockAndAddToCartButtonDisabled() {
        val product = coffee(stock = 0)
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(
                product = product,
                promotion = null
            )
        )

        composeRule.onNodeWithText(getString(R.string.detail_no_stock)).assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = PRODUCT_DETAIL_ADD_TO_CART).assertIsNotEnabled()
    }

    @Test
    fun givenProductDetailRendered_whenBackClicked_thenEmitBackCallback() {
        var backClicked = false
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(),
            onBack = { backClicked = true }
        )

        composeRule.onNodeWithTag(testTag = TOP_APP_BAR).performClick()

        assertTrue(backClicked)
    }

    @Test
    fun givenProductDetailRendered_whenAddToCartClicked_thenEmitAddToCartCallback() {
        var addToCartClicked = false
        createProductDetailScreen(
            uiState = ProductDetailUiStateMother.success(),
            onAddToCart = { addToCartClicked = true }
        )

        composeRule.onNodeWithTag(testTag = PRODUCT_DETAIL_ADD_TO_CART).performClick()

        assertTrue(addToCartClicked)
    }
}
