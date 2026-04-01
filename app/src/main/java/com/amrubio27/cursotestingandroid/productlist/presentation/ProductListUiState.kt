package com.amrubio27.cursotestingandroid.productlist.presentation

import com.amrubio27.cursotestingandroid.productlist.domain.model.Product
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
        val products: List<Product>, //de momento usamos el modelo de dominio, luego creamos uno de ui
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOption
    ) : ProductListUiState()

}