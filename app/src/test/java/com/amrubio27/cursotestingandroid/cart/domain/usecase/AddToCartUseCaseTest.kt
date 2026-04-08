package com.amrubio27.cursotestingandroid.cart.domain.usecase

import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.fakes.FakeCartItemRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddToCartUseCaseTest {
    @Test
    fun zero_quantity_throws_QuantityMustBePositive() = runTest {
        //Given
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(emptyList())
        }
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
        val productRepository = FakeProductRepository()
        val addToCartUseCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        val exception = runCatching { addToCartUseCase("id", 1) }.exceptionOrNull()

        //Then
        assert(exception is AppError.NotFoundError)
    }
}