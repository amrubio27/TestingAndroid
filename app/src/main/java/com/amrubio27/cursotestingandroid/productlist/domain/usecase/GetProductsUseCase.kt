package com.amrubio27.cursotestingandroid.productlist.domain.usecase

import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.PromotionRepository
import com.amrubio27.cursotestingandroid.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProduct: GetPromotionForProduct,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<List<ProductWithPromotion>> {
        return combine(
            productRepository.getProducts(),
            promotionRepository.getActivePromotions(),
            settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->
            val now = Instant.now()
            val activePromotions = promotions.filter {
                it.startTime.isBefore(now) && it.endTime.isAfter(now)
            }

            val filteredProducts = if (inStockOnly) {
                products.filter { it.stock > 0 }
            } else {
                products
            }

            filteredProducts.map { product ->
                val promotion = getPromotionForProduct(product, activePromotions)
                ProductWithPromotion(product, promotion)

            }
        }
    }
}