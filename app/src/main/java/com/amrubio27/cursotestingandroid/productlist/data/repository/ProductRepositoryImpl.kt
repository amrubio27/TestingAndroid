package com.amrubio27.cursotestingandroid.productlist.data.repository

import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.productlist.data.remote.RemoteDataSource
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource,
    val dispatchersProvider: DispatchersProvider
) : ProductRepository {
    override fun getProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override fun getProductById(id: String): Flow<Product?> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProducts() {
        withContext(dispatchersProvider.io) {
            val result = remoteDataSource.getProducts()
        }
    }
}