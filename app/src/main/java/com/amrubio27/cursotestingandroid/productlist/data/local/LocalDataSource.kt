package com.amrubio27.cursotestingandroid.productlist.data.local

import com.amrubio27.cursotestingandroid.cart.data.local.database.dao.CartItemDao
import com.amrubio27.cursotestingandroid.cart.data.local.database.entity.CartItemEntity
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.ProductDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.PromotionDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.ProductEntity
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao,
    private val cartItemDao: CartItemDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getProducts()
    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()


    suspend fun saveProducts(products: List<ProductEntity>) = productDao.replaceAll(products)

    suspend fun savePromotions(promotions: List<PromotionEntity>) =
        promotionDao.replaceAll(promotions)

    fun getProductById(id: String): Flow<ProductEntity?> = productDao.getProductById(id)

    fun getAllCartItems(): Flow<List<CartItemEntity>> = cartItemDao.getAllCartItems()

    suspend fun getCartItemById(productId: String): CartItemEntity? =
        cartItemDao.getCartItemById(productId)

    suspend fun updateCartItem(cartItemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.updateCartItem(cartItemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCartItem(cartItemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.deleteCartItem(cartItemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return try {
            cartItemDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCartItem(itemEntity: CartItemEntity): Result<Unit> {
        return try {
            cartItemDao.insertCartItem(itemEntity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}