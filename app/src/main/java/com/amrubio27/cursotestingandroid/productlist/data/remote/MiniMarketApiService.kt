package com.amrubio27.cursotestingandroid.productlist.data.remote

import com.amrubio27.cursotestingandroid.checkout.data.remote.response.OrderConfirmationResponse
import com.amrubio27.cursotestingandroid.productlist.data.remote.response.ProductsResponse
import com.amrubio27.cursotestingandroid.productlist.data.remote.response.PromotionsResponse
import retrofit2.http.GET

interface MiniMarketApiService {
    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponse

    @GET("data/promotions.json")
    suspend fun getPromotions(): PromotionsResponse

    @GET("data/order_confirmation.json")
    suspend fun placeOrder(): OrderConfirmationResponse
}
