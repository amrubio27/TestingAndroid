package com.amrubio27.cursotestingandroid.productlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import com.amrubio27.cursotestingandroid.productlist.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProductListEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProductListEvent> = _events

    private val _filtersVisible = MutableStateFlow<Boolean>(true)
    val filtersVisible: StateFlow<Boolean> = _filtersVisible.asStateFlow()


    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        getProductsUseCase()
            .onEach { products: List<Product> ->
                val categories = products.map { it.category }.distinct().sorted()
                _uiState.value =
                    ProductListUiState.Success(
                        products = products,
                        categories = categories,
                        selectedCategory = null,
                        sortOption = SortOption.NONE
                    )
            }
            .catch { exception: Throwable ->
                _uiState.value = ProductListUiState.Error(exception.message.orEmpty())
            }
            .launchIn(viewModelScope)

    }

    fun setCategory(category: String?) {
        viewModelScope.launch {
            //llamar al settingsRepository
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            //llamar al settingsRepository
        }
    }

    fun setFilterVisible(showFilter: Boolean) {
        viewModelScope.launch {
            _filtersVisible.value = showFilter
        }
    }
}