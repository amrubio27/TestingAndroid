package com.amrubio27.cursotestingandroid.productlist.domain.usecase

import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        //de momento, ahora no seria necesario pero para más adelante si nos hace falta el caso de uso
        return productRepository.getProducts()
    }
}