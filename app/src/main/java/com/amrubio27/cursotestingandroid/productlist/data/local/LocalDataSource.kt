package com.amrubio27.cursotestingandroid.productlist.data.local

import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.ProductDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.dao.PromotionDao
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getProducts()

    suspend fun saveProducts(products: List<ProductEntity>) = productDao.replaceAll(products)

}