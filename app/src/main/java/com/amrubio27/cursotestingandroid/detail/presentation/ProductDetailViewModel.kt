package com.amrubio27.cursotestingandroid.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.cart.domain.usecase.AddToCartUseCase
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.domain.model.AppError.DataBaseError
import com.amrubio27.cursotestingandroid.core.domain.model.AppError.NetworkError
import com.amrubio27.cursotestingandroid.core.domain.model.AppError.NotFoundError
import com.amrubio27.cursotestingandroid.core.domain.model.AppError.UnknownError
import com.amrubio27.cursotestingandroid.core.domain.model.AppError.Validation
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase,
    val addToCartUseCase: AddToCartUseCase
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
            if (e is AppError) {
                handleError(e)
            } else {
                handleError(AppError.UnknownError(e.message))
            }
        }.launchIn(viewModelScope)
    }

    fun addToCart() {
        val product = _uiState.value.item?.product?.id ?: return
        viewModelScope.launch {
            try {
                addToCartUseCase(product)
                _events.emit(ProductDetailEvent.SUCCESS_ADD_TO_CART)
            } catch (e: AppError) {
                handleError(e)
            } catch (e: Exception) {
                handleError(AppError.UnknownError(e.message))
            }
        }
    }

    private suspend fun handleError(e: AppError) {
        val newEvent = when (e) {
            NetworkError -> ProductDetailEvent.NETWORK_ERROR
            is Validation.InsufficientStock -> ProductDetailEvent.INSUFFICIENT_STOCK_ERROR
            is UnknownError, DataBaseError, NotFoundError, Validation.QuantityMustBePositive -> ProductDetailEvent.UNKNOWN_ERROR
        }
        _events.emit(newEvent)
    }

}