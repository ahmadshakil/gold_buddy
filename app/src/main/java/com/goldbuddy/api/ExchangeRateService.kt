package com.goldbuddy.api

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateService {
    @GET("v6/{apiKey}/pair/USD/PKR")
    suspend fun getExchangeRate(
        @Path("apiKey") apiKey: String
    ): ExchangeRateResponse
}

data class ExchangeRateResponse(
    val conversion_rate: Double,
    val base_code: String,
    val target_code: String
)
