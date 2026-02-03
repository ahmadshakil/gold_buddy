package com.goldbuddy.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoldRepository(
    private val goldApiService: GoldApiService,
    private val exchangeRateService: ExchangeRateService,
    private val goldApiKey: String,
    private val exchangeRateApiKey: String
) {
    private val TROY_OUNCE_TO_GRAMS = 31.1034768
    private val TOLA_TO_GRAMS = 11.665

    fun getGoldRates(): Flow<GoldRatesState> = flow {
        emit(GoldRatesState.Loading)
        try {
            val goldResponse = goldApiService.getGoldPrice(apiKey = goldApiKey)
            val exchangeResponse = exchangeRateService.getExchangeRate(apiKey = exchangeRateApiKey)

            val pricePerGramUsd = goldResponse.price / TROY_OUNCE_TO_GRAMS
            val pkrRate = exchangeResponse.conversion_rate

            val pricePerGramPkr = pricePerGramUsd * pkrRate
            val pricePer10GramsPkr = pricePerGramPkr * 10
            val pricePerTolaPkr = pricePerGramPkr * TOLA_TO_GRAMS

            emit(GoldRatesState.Success(
                pricePerGram = pricePerGramPkr,
                pricePer10Grams = pricePer10GramsPkr,
                pricePerTola = pricePerTolaPkr,
                lastUpdated = goldResponse.timestamp
            ))
        } catch (e: Exception) {
            emit(GoldRatesState.Error(e.message ?: "Unknown error occurred"))
        }
    }
}

sealed class GoldRatesState {
    object Loading : GoldRatesState()
    data class Success(
        val pricePerGram: Double,
        val pricePer10Grams: Double,
        val pricePerTola: Double,
        val lastUpdated: Long
    ) : GoldRatesState()
    data class Error(val message: String) : GoldRatesState()
}
