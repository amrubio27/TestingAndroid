package com.amrubio27.cursotestingandroid.cart.domain.usecase

import com.amrubio27.cursotestingandroid.core.builders.cartItem
import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.fakes.FakeCartItemRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCartItemUseCaseTest {
    @Test
    fun negative_quantity_throws_QuantityMustBePositive() = runTest {
        //Given
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository()
        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { useCase("id", -2) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun zero_quantity_remove_item_from_cart() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(10)
        }
        val cartItemProduct = cartItem {
            withProductId(productId)
            withQuantity(1)
        }

        val cartItemRepository = FakeCartItemRepository().apply {
            setCartItems(listOf(cartItemProduct))
        }
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        //When
        useCase(productId, 0)

        //Then
        val items = cartItemRepository.getCartItems().first()
        assertEquals(0, items.size)

    }

    @Test
    fun non_existing_product_throws_NotFoundError() = runTest {
        //Given
        val productRepository = FakeProductRepository().apply {
            setProducts(emptyList())
        }
        val cartItemRepository = FakeCartItemRepository()
        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { useCase("id", 1) }.exceptionOrNull()

        //Then
        assert(exception is AppError.NotFoundError)
    }

    @Test
    fun quantity_greater_than_stock_throws_insufficient_stock() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(2)
        }
        val cartItemRepository = FakeCartItemRepository().apply {
            setCartItems(
                listOf(
                cartItem {
                    withProductId(productId)
                    withQuantity(1)
                }
            ))
        }
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //when
        val exception = runCatching {
            useCase(productId, 5)
        }.exceptionOrNull()

        //then
        assertTrue(exception is AppError.Validation.InsufficientStock)
    }

    @Test
    fun given_valid_product_and_quantity_when_invoke_then_updates_cart_item() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(20)
        }
        val cartItemRepository = FakeCartItemRepository().apply {
            setCartItems(
                listOf(
                cartItem {
                    withProductId(productId)
                    withQuantity(1)
                }
            ))
        }
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = UpdateCartItemUseCase(cartItemRepository, productRepository)

        //When
        useCase(productId, 5)

        //Then
        val items = cartItemRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(5, items.first().quantity)

    }

}