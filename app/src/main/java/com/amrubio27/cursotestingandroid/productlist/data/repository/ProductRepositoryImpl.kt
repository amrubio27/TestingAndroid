package com.amrubio27.cursotestingandroid.productlist.data.repository

import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.productlist.data.local.LocalDataSource
import com.amrubio27.cursotestingandroid.productlist.data.mappers.toDomain
import com.amrubio27.cursotestingandroid.productlist.data.mappers.toEntity
import com.amrubio27.cursotestingandroid.productlist.data.remote.RemoteDataSource
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    val remoteDataSource: RemoteDataSource,
    val localDataSource: LocalDataSource,
    val dispatchersProvider: DispatchersProvider
) : ProductRepository {
    private val refreshScope = CoroutineScope(SupervisorJob() + dispatchersProvider.io)
    private val refreshMutex = Mutex()


    override fun getProducts(): Flow<List<Product>> {
        return localDataSource.getAllProducts()
            .map { entities ->
                entities.mapNotNull {
                    it.toDomain()
                }
            }
            .onStart {
                refreshScope.launch {
                    if (!refreshMutex.tryLock()) return@launch
                    try {
                        refreshProducts()
                    } catch (e: Exception) {
                    } finally {
                        refreshMutex.unlock()
                    }

                }
            }
            .catch {
                //Log
            }
    }

    override fun getProductById(id: String): Flow<Product?> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshProducts() {
        withContext(dispatchersProvider.io) {
            val products = remoteDataSource.getProducts().getOrThrow()
            val productsEntity = products.map { it.toEntity() }
            localDataSource.saveProducts(productsEntity)

        }
    }
}