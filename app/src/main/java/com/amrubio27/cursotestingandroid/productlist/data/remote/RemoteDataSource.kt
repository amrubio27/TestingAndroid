package com.amrubio27.cursotestingandroid.productlist.data.remote

import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.productlist.data.remote.response.ProductResponse
import com.amrubio27.cursotestingandroid.productlist.data.remote.response.PromotionResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    val miniMarketApiService: MiniMarketApiService
) {
    suspend fun getProducts(): Result<List<ProductResponse>> {
        return try {
            val response = miniMarketApiService.getProducts()
            Result.success(response.products)
        } catch (e: Exception) {
            Result.failure(exception = mapToDomainError(e))
        }
    }

    suspend fun getPromotions(): Result<List<PromotionResponse>> {
        return try {
            val response = miniMarketApiService.getPromotions()
            Result.success(response.promotions)
        } catch (e: Exception) {
            Result.failure(exception = mapToDomainError(e))
        }
    }

    private fun mapToDomainError(e: Exception): AppError {
        return when (e) {
            is UnknownHostException -> AppError.NetworkError
            is SocketTimeoutException -> AppError.NetworkError
            is IOException -> AppError.NetworkError
            is HttpException -> {
                when (e.code()) {
                    404 -> AppError.NotFoundError
                    else -> AppError.NetworkError
                }
            }

            else -> AppError.UnknownError(e.message)
        }
    }
}