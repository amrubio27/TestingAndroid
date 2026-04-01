package com.amrubio27.cursotestingandroid.productlist.domain.repository

import com.amrubio27.cursotestingandroid.productlist.domain.model.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getActivePromotions(): Flow<List<Promotion>>
    suspend fun refreshPromotions()
}