package com.amrubio27.cursotestingandroid.core.stubs

import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FailingProductRepositoryStub(
    private val exception: Throwable
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> = flow { throw exception }

    override fun getProductById(id: String): Flow<Product?> = flowOf()

    override suspend fun refreshProducts() {}

    override fun getProductsByIds(ids: Set<String>): Flow<List<Product>> = flowOf()

}