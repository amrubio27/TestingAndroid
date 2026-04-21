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
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    val getProductDetailWithPromotionUseCase: GetProductDetailWithPromotionUseCase,
    val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _events = MutableSharedFlow<ProductDetailEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductDetailEvent> = _events

    private val loadTrigger = MutableSharedFlow<String>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProductDetailUiState> = loadTrigger
        .flatMapLatest { productId ->
            getProductDetailWithPromotionUseCase(productId)
                .map<ProductWithPromotion?, ProductDetailUiState> { product ->
                    ProductDetailUiState(item = product, isLoading = false)
                }
                .onStart { emit(ProductDetailUiState(isLoading = true)) }
                .catch { e ->
                    val appError = if (e is AppError) e else AppError.UnknownError(e.message)
                    handleError(appError)
                    emit(ProductDetailUiState(item = null, isLoading = false))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProductDetailUiState(isLoading = true)
        )

    fun loadProduct(productId: String) {
        loadTrigger.tryEmit(productId)
    }

    fun addToCart() {
        val product = uiState.value.item?.product?.id ?: return
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