package com.amrubio27.cursotestingandroid.productlist.domain.repository

import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(id: String): Flow<Product?>
    suspend fun refreshProducts()

    fun getProductsByIds(ids: Set<String>): Flow<List<Product>>

}