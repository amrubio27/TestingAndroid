package com.amrubio27.cursotestingandroid.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.detail.domain.usecase.GetProductDetailWithPromotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    private var productJob: Job? = null

    fun loadProduct(productId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        productJob?.cancel()
        productJob = getProductDetailWithPromotionUseCase(productId).onEach { product ->
            _uiState.value = _uiState.value.copy(isLoading = false, item = product)
        }.catch { e: Throwable ->
            _uiState.value = _uiState.value.copy(isLoading = false)
            _events.emit(ProductDetailEvent.ShowError(e.message))
        }.launchIn(viewModelScope)
    }

    fun addToCart() {

    }

}