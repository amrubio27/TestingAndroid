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

        val percentPromos = productPromos.filter {
            it.type == PromotionType.PERCENT
        }.maxByOrNull { it.value }

        if (percentPromos != null) {
            val percent = percentPromos.value.coerceIn(0.0, 100.0)
            val discountPrice = product.price * (1 - percent / 100.0).roundTo2Decimals()
            return ProductPromotion.Percent(percent = percent, discountPrice = discountPrice)
        }

        val buyPayPromo = productPromos.firstOrNull() { it.type == PromotionType.BUY_X_PAY_Y }
        if (buyPayPromo != null) {
            val buy = buyPayPromo.buyQuantity ?: return null
            val pay = buyPayPromo.value.toInt().coerceIn(0, buy)

            return ProductPromotion.BuyXPayY(
                buy = buy, pay = pay, label = "{$buy}x{$pay}"
            )
        }
        return null
    }
}