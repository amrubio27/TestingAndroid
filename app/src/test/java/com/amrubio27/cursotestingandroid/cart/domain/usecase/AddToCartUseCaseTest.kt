package com.amrubio27.cursotestingandroid.cart.domain.usecase

import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.fakes.FakeCartItemRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToCartUseCaseTest {
    @Test
    fun zero_quantity_throws_QuantityMustBePositive() = runTest {
        //Given
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository()
        val addToCartUseCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { addToCartUseCase("id", 0) }.exceptionOrNull()

        //Then
        assert(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun negative_quantity_throws_QuantityMustBePositive() = runTest {
        //Given
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository()
        val addToCartUseCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { addToCartUseCase("id", -2) }.exceptionOrNull()

        //Then
        assert(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun non_existing_product_throws_NotFoundError() = runTest {
        //Given
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(emptyList())
        }
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { useCase("id", 1) }.exceptionOrNull()

        //Then
        assert(exception is AppError.NotFoundError)
    }

    @Test
    fun insufficient_stock_throws_InsufficientStock() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(2)
        }
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //when
        val exception = runCatching {
            useCase(productId, 5)
        }.exceptionOrNull()

        //then
        assert(exception is AppError.Validation.InsufficientStock)
        assertEquals(2, (exception as AppError.Validation.InsufficientStock).available)
    }
}