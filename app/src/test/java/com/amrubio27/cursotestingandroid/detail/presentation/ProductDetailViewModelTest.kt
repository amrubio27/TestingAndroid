package com.amrubio27.cursotestingandroid.detail.presentation

import app.cash.turbine.test
import com.amrubio27.cursotestingandroid.cart.domain.usecase.AddToCartUseCase
import com.amrubio27.cursotestingandroid.core.MainDispatcherRule
import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.fakes.FakeCartItemRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakePromotionRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeSystemClock
import com.amrubio27.cursotestingandroid.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import com.amrubio27.cursotestingandroid.productlist.domain.usecase.GetPromotionForProduct
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeProduct = FakeProductRepository()
    private val fakeCart = FakeCartItemRepository()
    private val fakePromotion = FakePromotionRepository()
    private val fakeClock: FakeSystemClock = FakeSystemClock()

    private fun createViewModel() = ProductDetailViewModel(
        getProductDetailWithPromotionUseCase = GetProductDetailWithPromotionUseCase(
            fakeProduct,
            fakePromotion,
            GetPromotionForProduct(),
            fakeClock
        ),
        addToCartUseCase = AddToCartUseCase(
            fakeCart,
            fakeProduct
        )
    )

    @Test
    fun `GIVEN valid product id WHEN load product THEN emits item`() =
        runTest(mainDispatcherRule.scheduler) {
            //Given
            val productId = "p1"
            val p = product {
                withId(productId); withName("miguel")
            }
            fakeProduct.setProducts(listOf(p))

            val viewModel = createViewModel()

            viewModel.uiState.test {
                awaitItem()
                //when
                viewModel.loadProduct(productId)

                //then
                val finalState = awaitItem()

                assertEquals(productId, finalState.item?.product?.id)
                assertEquals("miguel", finalState.item?.product?.name)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN missing product id WHEN load product THEN ends with item null`() =
        runTest(mainDispatcherRule.scheduler) {
            //Given
            fakeProduct.setProducts(emptyList())
            val viewModel = createViewModel()

            viewModel.uiState.test {
                awaitItem()

                viewModel.loadProduct("missing_item")

                val state = awaitItem()
                assertNull(state.item)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN loaded product WHEN add to cart succeeds THEN emits succes event`() =
        runTest(mainDispatcherRule.scheduler) {
            val p = product { withId("1"); withStock(10) }
            fakeProduct.setProducts(listOf(p))
            val viewModel = createViewModel()

            viewModel.loadProduct("1")

            viewModel.events.test {
                viewModel.addToCart()
                val result = awaitItem()
                assertEquals(ProductDetailEvent.SUCCESS_ADD_TO_CART, result)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN loaded product without stock WHEN add to cart THEN emits insufficient stock error`() =
        runTest(mainDispatcherRule.scheduler) {
            val p = product { withId("1"); withStock(0) }
            fakeProduct.setProducts(listOf(p))
            val viewModel = createViewModel()

            viewModel.loadProduct("1")

            viewModel.events.test {
                viewModel.addToCart()
                val result = awaitItem()
                assertEquals(ProductDetailEvent.INSUFFICIENT_STOCK_ERROR, result)
                cancelAndIgnoreRemainingEvents()
            }

        }


}