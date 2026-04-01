package com.amrubio27.cursotestingandroid.productlist.data.mappers

import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.PromotionEntity
import com.amrubio27.cursotestingandroid.productlist.data.remote.response.PromotionResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

fun PromotionResponse.toEntity(json: Json): PromotionEntity? {
    if (startAtEpoch == null || endAtEpoch == null) return null

    val productIds = listOf(productId)
    val productIdsJson = json.encodeToString(
        ListSerializer(String.serializer()),
        productIds
    )

    return PromotionEntity(
        id = id,
        type = type,
        productIds = productIdsJson,
        percent = percent,
        buyX = buyX,
        payY = payY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch
    )
}