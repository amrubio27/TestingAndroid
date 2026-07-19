package com.amrubio27.cursotestingandroid.cart.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.amrubio27.cursotestingandroid.R
import com.amrubio27.cursotestingandroid.cart.presentation.model.CartItemWithPromotion
import com.amrubio27.cursotestingandroid.core.mothers.CartUiStateMother.cartItemWithPromotion
import com.amrubio27.cursotestingandroid.core.mothers.CartUiStateMother.cartSuccess
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.bread
import com.amrubio27.cursotestingandroid.core.mothers.ProductMother.coffee
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_EMPTY
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_LOADING
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_RETRY
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.cartItem
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.cartQuantityDecrease
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.cartQuantityIncrease
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class CartScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createCartScreen(
        state: CartUiState,
        onBack: () -> Unit = {},
        onRetrySelected: () -> Unit = {},
        onIncreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onDecreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onRemove: (String) -> Unit = {},
        onNavigateToCheckOut: () -> Unit = {},
    ) {
        composeRule.setContent {
            CartContent(
                uiState = state,
                onBack = onBack,
                onRetrySelected = onRetrySelected,
                onRemove = onRemove,
                onDecreaseQuantity = onDecreaseQuantity,
                onIncreaseQuantity = onIncreaseQuantity,
                navigateToCheckOut = onNavigateToCheckOut,
            )
        }
    }

    private fun getString(resId: Int): String = composeRule.activity.getString(resId)

    private fun getString(
        resId: Int,
        vararg formatArgs: Any,
    ): String = composeRule.activity.getString(resId, *formatArgs)

    @Test
    fun givenLoadingState_whenRendered_thenShowProgressView() {
        createCartScreen(state = CartUiState.Loading)

        composeRule.onNodeWithTag(testTag = CART_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowTextAndRetryButton() {
        val errorText = "Prueba error"
        createCartScreen(state = CartUiState.Error(errorText))

        composeRule
            .onNodeWithText(errorText, substring = true, ignoreCase = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_retry_button)).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRetryClicked_thenEmitsRetryCallback() {
        var retryClicked: Boolean = false

        val errorText = "Prueba error"
        createCartScreen(
            state = CartUiState.Error(errorText),
            onRetrySelected = { retryClicked = true },
        )

        composeRule.onNodeWithTag(testTag = CART_RETRY).performClick()

        assertTrue(retryClicked)
    }

    @Test
    fun givenEmptySuccessState_whenRendered_thenShowsEmptyCartMessage() {
        createCartScreen(
            state =
                CartUiState.Success(
                    summary = null,
                    isLoading = false,
                    cartItems = emptyList(),
                ),
        )

        composeRule.onNodeWithTag(testTag = CART_EMPTY).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_empty_subtitle)).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowsItemsQuantitiesAndSummary() {
        createCartScreen(state = cartSuccess())

        composeRule.onNodeWithText(coffee().name).assertIsDisplayed()
        composeRule.onNodeWithText(bread().name).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_subtotal_label)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_discount_label)).assertIsDisplayed()
        composeRule.onNodeWithText(getString(R.string.cart_total_label)).assertIsDisplayed()

        composeRule.onNodeWithTag(testTag = cartItem(productId = coffee().id)).assertIsDisplayed()
        composeRule.onNodeWithTag(testTag = cartItem(productId = bread().id)).assertIsDisplayed()
    }

    @Test
    fun givenInitialQuantity_whenIncreaseClicked_thenEmitsIncreaseQuantity() {
        var emitted: Pair<String, Int>? = null
        val initialQuantity = 2

        createCartScreen(
            state =
                cartSuccess(
                    cartItems =
                        listOf(
                            cartItemWithPromotion(
                                product = bread(),
                                quantity = initialQuantity,
                            ),
                        ),
                ),
            onIncreaseQuantity = { productId, quantity -> emitted = productId to quantity },
        )

        composeRule
            .onNodeWithTag(testTag = cartQuantityIncrease(productId = bread().id))
            .assertIsEnabled()
            .performClick()

        assertEquals(expected = bread().id to (initialQuantity), actual = emitted)
    }

    @Test
    fun givenInitialQuantity_whenDecreaseClicked_thenEmitsDecreaseQuantity() {
        var emitted: Pair<String, Int>? = null
        val initialQuantity = 3

        createCartScreen(
            state =
                cartSuccess(
                    cartItems =
                        listOf(
                            cartItemWithPromotion(
                                product = bread(),
                                quantity = initialQuantity,
                            ),
                        ),
                ),
            onDecreaseQuantity = { productId, quantity -> emitted = productId to quantity },
        )

        composeRule
            .onNodeWithTag(testTag = cartQuantityDecrease(productId = bread().id))
            .assertIsEnabled()
            .performClick()

        assertEquals(expected = bread().id to (initialQuantity), actual = emitted)
    }

    @Test
    fun givenCartItem_whenSwipedRight_thenEmitsRemoveCallback() {
        var removeProductId: String? = null

        createCartScreen(
            state =
                cartSuccess(
                    cartItems =
                        listOf(
                            cartItemWithPromotion(
                                product = bread(),
                                quantity = 2,
                            ),
                        ),
                ),
            onRemove = { removeProductId = it },
        )

        composeRule
            .onNodeWithTag(cartItem(bread().id))
            .performTouchInput { swipeRight() }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            removeProductId != null
        }

        assertEquals(bread().id, removeProductId)
    }

    @Test
    fun givenItemsAtStockEdges_whenRendered_thenInvalidControlsAreDisable() {
        val fullStockItem: CartItemWithPromotion =
            cartItemWithPromotion(
                product = bread(stock = 7),
                quantity = 7,
            )

        createCartScreen(state = cartSuccess(cartItems = listOf(fullStockItem)))

        composeRule
            .onNodeWithTag(testTag = cartQuantityIncrease(productId = bread().id))
            .assertIsNotEnabled()
    }
}
