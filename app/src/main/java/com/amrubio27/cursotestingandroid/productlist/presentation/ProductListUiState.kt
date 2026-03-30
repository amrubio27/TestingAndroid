package com.amrubio27.cursotestingandroid.productlist.presentation

sealed class ProductListUiState {
    data object Loading : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
    data class Success(
        //val products: List<>,
        //val categories:List<>.
        val selectedCategory: String,
        //sortOption
    ) : ProductListUiState()

}