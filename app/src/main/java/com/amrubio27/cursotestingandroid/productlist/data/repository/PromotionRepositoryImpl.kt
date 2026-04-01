package com.amrubio27.cursotestingandroid.productlist.data.repository

import com.amrubio27.cursotestingandroid.core.domain.coroutines.DispatchersProvider
import com.amrubio27.cursotestingandroid.productlist.data.local.LocalDataSource
import com.amrubio27.cursotestingandroid.productlist.data.local.database.entity.PromotionEntity
import com.amrubio27.cursotestingandroid.productlist.data.mappers.toEntity
import com.amrubio27.cursotestingandroid.productlist.data.remote.RemoteDataSource
import com.amrubio27.cursotestingandroid.productlist.domain.model.Promotion
import com.amrubio27.cursotestingandroid.productlist.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PromotionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val dispatchersProvider: DispatchersProvider,
    private val json: Json
) : PromotionRepository {
    override fun getActivePromotions(): Flow<List<Promotion>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshPromotions() {
        withContext(dispatchersProvider.io) {
            val promotions = remoteDataSource.getPromotions().getOrThrow()
            val promotionsEntity: List<PromotionEntity> =
                promotions.mapNotNull { it.toEntity(json) }
            localDataSource.savePromotions(promotionsEntity)

        }
    }
}