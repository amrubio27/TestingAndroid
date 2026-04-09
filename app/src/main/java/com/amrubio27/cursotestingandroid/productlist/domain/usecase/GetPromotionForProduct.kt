package com.amrubio27.cursotestingandroid.productlist.domain.usecase

import com.amrubio27.cursotestingandroid.core.presentation.ext.roundTo2Decimals
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.Promotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.PromotionType
import javax.inject.Inject

class GetPromotionForProduct @Inject constructor() {
    operator fun invoke(product: Product, promotions: List<Promotion>): ProductPromotion? {
        val productPromos = promotions.filter {
            it.productIds.contains(product.id)
        }

        val buyPayPromo = productPromos.firstOrNull { it.type == PromotionType.BUY_X_PAY_Y }
        if (buyPayPromo != null) {
            val buy = buyPayPromo.buyQuantity ?: return null
            val pay = buyPayPromo.value.toInt().coerceIn(0, buy)

            return ProductPromotion.BuyXPayY(
                buy = buy,
                pay = pay,
                label = "${buy}x${pay}"
            )
        }

        val percentPromo = productPromos.filter {
            it.type == PromotionType.PERCENT
        }.maxByOrNull { it.value }

        if (percentPromo != null) {
            val percent = percentPromo.value.coerceIn(0.0, 100.0)
            val discountPrice = product.price * (1 - percent / 100.0).roundTo2Decimals()
            return ProductPromotion.Percent(percent = percent, discountedPrice = discountPrice)
        }

        return null
    }
}

//O(n * m) a O(n + m)
/**
 * private fun List<Promotion>.getPromotionsByProductId(): Map<String, List<Promotion>> {
 *     val mapPromotion = mutableMapOf<String, List<Promotion>>()
 *     forEach { promotion ->
 *         promotion.productsIds.forEach { productId ->
 *             mapPromotion[productId] =
 *                 mapPromotion.getOrDefault(productId, emptyList()) + promotion
 *         }
 *     }
 *     return mapPromotion
 * }
 *
 * class GetPromotionForProduct @Inject constructor() {
 * operator fun invoke(
 *         product: Product,
 *         promotions: Map<String, List<Promotion>>
 *     ): ProductPromotion? {
 *         val productPromos = promotions.getOrDefault(product.id, emptyList())
 *    ...
 *    }
 * }
 */