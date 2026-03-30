package com.amrubio27.cursotestingandroid.productlist.presentation

sealed interface ProductListEvent {
    data class ShowMessage(val message: String) : ProductListEvent
}