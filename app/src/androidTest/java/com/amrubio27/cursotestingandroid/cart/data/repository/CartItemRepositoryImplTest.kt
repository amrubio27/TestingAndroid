package com.amrubio27.cursotestingandroid.cart.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amrubio27.cursotestingandroid.cart.domain.repository.CartItemRepository
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CartItemRepositoryImplTest {

    @get:Rule
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var cartItemRepository: CartItemRepository

    @Before
    fun setUp() = runTest {
        hilt.inject()
        cartItemRepository.clearCart()
    }

    @Test
    fun givenNoData_whenGetCartItems_thenReturnsEmptyList() = runTest {
        val items = cartItemRepository.getCartItems().first()
        assertTrue(items.isEmpty())
    }

    //esencial
    @Test
    fun givenItem_whenAddToCart_thenPersistItem() = runTest {
        cartItemRepository.addToCart("p1", 2)

        val items = cartItemRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals("p1", items[0].productId)
        assertEquals(2, items[0].quantity)
    }

    //esencial
    @Test
    fun givenExistingItem_whenAddToCart_thenIncrementQuantity() = runTest {
        cartItemRepository.addToCart("p1", 2)
        cartItemRepository.addToCart("p1", 3)

        val item = cartItemRepository.getCartItemById("p1")
        assertEquals(5, item?.quantity)
    }

    @Test
    fun givenItemInCart_whenRemoveFromCart_thenItemIsDeleted() = runTest {
        cartItemRepository.addToCart("p1", 2)
        cartItemRepository.removeFromCart("p1")

        val items = cartItemRepository.getCartItems().first()
        assertTrue(items.isEmpty())
    }

    //esencial
    @Test(expected = AppError.NotFoundError::class)
    fun givenNoItem_whenRemoveFromCart_thenThrowsNotFoundError() = runTest {
        cartItemRepository.removeFromCart("non_existent")
    }

    //esencial
    @Test
    fun givenItemInCart_whenUpdateQuantity_thenQuantityIsUpdated() = runTest {
        cartItemRepository.addToCart("p1", 2)
        cartItemRepository.updateQuantity("p1", 10)

        val item = cartItemRepository.getCartItemById("p1")
        assertEquals(10, item?.quantity)
    }

    @Test(expected = AppError.NotFoundError::class)
    fun givenNoItem_whenUpdateQuantity_thenThrowsNotFoundError() = runTest {
        cartItemRepository.updateQuantity("non_existent", 5)
    }

    //esencial
    @Test
    fun givenItemsInCart_whenClearCart_thenCartIsEmpty() = runTest {
        cartItemRepository.addToCart("p1", 2)
        cartItemRepository.addToCart("p2", 1)

        cartItemRepository.clearCart()

        val items = cartItemRepository.getCartItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun givenItem_whenGetCartItemById_thenReturnsItem() = runTest {
        cartItemRepository.addToCart("p1", 5)

        val item = cartItemRepository.getCartItemById("p1")
        assertEquals("p1", item?.productId)
        assertEquals(5, item?.quantity)
    }

    @Test
    fun givenNoItem_whenGetCartItemById_thenReturnsNull() = runTest {
        val item = cartItemRepository.getCartItemById("non_existent")
        assertNull(item)
    }
}
