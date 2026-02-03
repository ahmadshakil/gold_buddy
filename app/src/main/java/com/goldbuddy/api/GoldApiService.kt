package com.goldbuddy.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GoldApiService {
    @GET("api/{symbol}/{curr}")
    suspend fun getGoldPrice(
        @Path("symbol") symbol: String = "XAU",
        @Path("curr") currency: String = "USD",
        @Header("x-access-token") apiKey: String
    ): GoldPriceResponse
}

data class GoldPriceResponse(
    val price: Double,
    val currency: String,
    val timestamp: Long
)
