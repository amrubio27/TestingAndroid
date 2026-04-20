package com.amrubio27.cursotestingandroid.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductWithPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import com.amrubio27.cursotestingandroid.productlist.domain.repository.SettingsRepository
import com.amrubio27.cursotestingandroid.productlist.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor(
    getProductsUseCase: GetProductsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<ProductListUiState> = combine(
        getProductsUseCase(), settingsRepository.selectedCategory, settingsRepository.sortOption
    ) { products, selectedCategory, sortOption ->
        var filteredProducts = products
        if (selectedCategory != null) {
            filteredProducts = filteredProducts.filter { it.product.category == selectedCategory }
        }

        val sorted = when (sortOption) {
            SortOption.NONE -> filteredProducts
            SortOption.PRICE_ASC -> filteredProducts.sortedBy { effecticePrice(it) }
            SortOption.PRICE_DESC -> filteredProducts.sortedByDescending { effecticePrice(it) }
            SortOption.DISCOUNT -> filteredProducts.sortedWith(compareByDescending<ProductWithPromotion> {
                effectiveDiscountPercent(it)
            }.thenBy {
                it.promotion == null
            })
        }

        val categories = products.map { it.product.category }.distinct().sorted()

        ProductListUiState.Success(
            products = sorted,
            categories = categories,
            selectedCategory = selectedCategory,
            sortOption = sortOption
        ) as ProductListUiState
    }.catch { exception: Throwable ->
        emit(ProductListUiState.Error(exception.message.orEmpty()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListUiState.Loading
    )

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    val filtersVisible: StateFlow<Boolean> = settingsRepository.filtersVisible.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
    )

    fun setCategory(category: String?) {
        viewModelScope.launch {
            settingsRepository.setSelectedCategory(category)
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            settingsRepository.setSortOption(sortOption)
        }
    }

    fun setFilterVisible(showFilter: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFiltersVisible(showFilter)
        }
    }

    private fun effectiveDiscountPercent(item: ProductWithPromotion): Double {
        return when (val promo = item.promotion) {
            is ProductPromotion.Percent -> promo.percent
            else -> 0.0
        }
    }

    private fun effecticePrice(item: ProductWithPromotion): Double {
        return when (val promo = item.promotion) {
            is ProductPromotion.Percent -> promo.discountedPrice
            else -> item.product.price
        }
    }

}