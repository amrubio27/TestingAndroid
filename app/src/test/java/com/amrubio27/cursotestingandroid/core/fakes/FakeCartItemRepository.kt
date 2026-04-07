package com.amrubio27.cursotestingandroid.core.fakes

import com.amrubio27.cursotestingandroid.cart.domain.model.CartItem
import com.amrubio27.cursotestingandroid.cart.domain.repository.CartItemRepository
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartItemRepository : CartItemRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> = _cartItems.asStateFlow()

    override suspend fun addToCart(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.indexOfFirst { it.productId == productId }

        if (existingItem >= 0) {
            val item = currentItems[existingItem]
            currentItems[existingItem] = item.copy(quantity = item.quantity + quantity)

        } else {
            currentItems.add(CartItem(productId, quantity))
        }
        _cartItems.value = currentItems
    }

    override suspend fun removeFromCart(productId: String) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.indexOfFirst { it.productId == productId }

        if (existingItem >= 0) {
            currentItems.removeAt(existingItem)
            _cartItems.value = currentItems
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.indexOfFirst { it.productId == productId }

        if (existingItem >= 0) {
            currentItems[existingItem] = currentItems[existingItem].copy(quantity = quantity)
            _cartItems.value = currentItems
        } else {
            throw AppError.NotFoundError
        }
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
    }

    override suspend fun getCartItemById(productId: String): CartItem? {
        return _cartItems.value.find { it.productId == productId }
    }
}