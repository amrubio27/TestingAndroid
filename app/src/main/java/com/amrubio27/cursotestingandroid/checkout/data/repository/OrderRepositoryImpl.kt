package com.amrubio27.cursotestingandroid.checkout.data.repository

import com.amrubio27.cursotestingandroid.checkout.data.mapper.toDomain
import com.amrubio27.cursotestingandroid.checkout.domain.model.OrderConfirmation
import com.amrubio27.cursotestingandroid.checkout.domain.repository.OrderRepository
import com.amrubio27.cursotestingandroid.productlist.data.remote.RemoteDataSource
import jakarta.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : OrderRepository {
    override suspend fun placeOrder(): OrderConfirmation {
        return remoteDataSource.placeOrder().getOrThrow().toDomain()
    }
}
