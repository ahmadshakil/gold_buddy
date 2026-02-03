package com.goldbuddy.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoldViewModel(private val repository: GoldRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<GoldRatesState>(GoldRatesState.Loading)
    val uiState: StateFlow<GoldRatesState> = _uiState

    init {
        refreshRates()
    }

    fun refreshRates() {
        viewModelScope.launch {
            repository.getGoldRates().collect {
                _uiState.value = it
            }
        }
    }
}
