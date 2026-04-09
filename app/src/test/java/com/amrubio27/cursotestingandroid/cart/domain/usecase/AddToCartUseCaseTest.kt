package com.amrubio27.cursotestingandroid.cart.domain.usecase

import com.amrubio27.cursotestingandroid.cart.domain.repository.CartItemRepository
import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.fakes.FakeCartItemRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    @Test
    fun succesfull_case_add_item_to_cart() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(10)
        }
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //when
        useCase(productId, 3)

        //then
        val items = cartItemRepository.getCartItems().first()
        assertEquals(productId, items.first().productId)
        assertEquals(1, items.size)
        assertEquals(3, items.first().quantity)
    }

    @Test
    fun default_quantity_adds_one_item() = runTest {
        //Given
        val productId = "id-test-1"
        val product = product {
            withId(productId)
            withStock(10)
        }
        val cartItemRepository = FakeCartItemRepository()
        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //when
        useCase(productId)

        //then
        val items = cartItemRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(1, items.first().quantity)
    }

    @Test
    fun zero_quantity_does_not_call_any_repository() = runTest {
        //Given
        val productRepository = mockk<ProductRepository>()
        val cartItemRepository = mockk<CartItemRepository>()
        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        runCatching { useCase("id", 0) }.exceptionOrNull()

        //Then
        coVerify(exactly = 0) { productRepository.getProductById(any()) }
        coVerify(exactly = 0) { cartItemRepository.getCartItemById(any()) }
        coVerify(exactly = 0) { cartItemRepository.addToCart(any(), any()) }
    }

    @Test
    fun valid_product_calls_addToCart_with_expect_values() = runTest {
        //Given
        val productRepository = mockk<ProductRepository>()
        val cartItemRepository =
            mockk<CartItemRepository>() //los relax = true pueden funcionar pero podriamos saltarnos casuisticas por lo que es mejor mockk con just Run en sitios especificos que nosotros sepamos

        val productId = "custom-id"
        val product = product {
            withId(productId)
            withStock(10)
        }

        coEvery { productRepository.getProductById(productId) } returns flowOf(product)
        coEvery { cartItemRepository.getCartItemById(productId) } returns null
        coEvery { cartItemRepository.addToCart(productId, 3) } just Runs

        val useCase = AddToCartUseCase(cartItemRepository, productRepository)

        //When
        useCase(productId, 3)

        //Then
        coVerify(exactly = 1) { productRepository.getProductById(productId) }
        coVerify(exactly = 1) { cartItemRepository.getCartItemById(productId) }
        coVerify(exactly = 1) { cartItemRepository.addToCart(productId, 3) }
    }
}