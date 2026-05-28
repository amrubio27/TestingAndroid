package com.amrubio27.cursotestingandroid.cart.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_EMPTY
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_LOADING
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.CART_RETRY
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import kotlin.test.Test

class CartScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createCartScreen(
        state: CartUiState,
        onBack: () -> Unit = {},
        onRetrySelected: () -> Unit = {},
        onIncreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onDecreaseQuantity: (String, Int) -> Unit = { _, _ -> },
        onRemove: (String) -> Unit = {}
    ) {
        composeRule.setContent {
            CartContent(
                uiState = state,
                onBack = onBack,
                onRetrySelected = onRetrySelected,
                onRemove = onRemove,
                onDecreaseQuantity = onDecreaseQuantity,
                onIncreaseQuantity = onIncreaseQuantity
            )
        }
    }

    @Test
    fun givenLoadingState_whenRendered_thenShowProgressView() {
        createCartScreen(state = CartUiState.Loading)

        composeRule.onNodeWithTag(testTag = CART_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowTextAndRetryButton() {
        val errorText = "Prueba error"
        createCartScreen(state = CartUiState.Error(errorText))

        composeRule.onNodeWithText(errorText, substring = true, ignoreCase = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRetryClicked_thenEmitsRetryCallback() {
        var retryClicked: Boolean = false

        val errorText = "Prueba error"
        createCartScreen(
            state = CartUiState.Error(errorText),
            onRetrySelected = { retryClicked = true }
        )

        composeRule.onNodeWithTag(testTag = CART_RETRY).performClick()

        assertTrue(retryClicked)
    }

    @Test
    fun givenEmptySuccessState_whenRendered_thenShowsEmptyCartMessage() {
        createCartScreen(
            state = CartUiState.Success(
                summary = null,
                isLoading = false,
                cartItems = emptyList()
            )
        )

        composeRule.onNodeWithTag(testTag = CART_EMPTY).assertIsDisplayed()
        composeRule.onNodeWithText("Tu carrito está vacío").assertIsDisplayed()
        composeRule.onNodeWithText("Agrega productos para comenzar").assertIsDisplayed()
    }

}