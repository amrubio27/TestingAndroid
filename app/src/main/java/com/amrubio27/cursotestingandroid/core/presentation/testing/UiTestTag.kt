package com.amrubio27.cursotestingandroid.core.presentation.testing

import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption

object UiTestTag {

    //TOP APP BAR
    const val TOP_APP_BAR = "top_app_bar"
    const val TOP_APP_BAR_BADGE = "top_app_bar_badge"
    const val TOP_APP_BAR_FILTER = "top_app_bar_filter"
    const val TOP_APP_BAR_SETTINGS = "top_app_bar_settings"
    const val TOP_APP_BAR_CART = "top_app_bar_cart"
    const val FILTER_VIEW = "filter_view"

    //SETTINGS
    const val SETTINGS_CONTENT = "settings_content"
    const val SETTINGS_IN_STOCK_SWITCH = "settings_in_stock_switch"
    const val SETTINGS_TAX_SWITCH = "settings_tax_switch"

    fun settingsThemeOption(themeModeName: String) = "settings_theme_${themeModeName.lowercase()}"

    // PRODUCT LIST
    const val PRODUCT_LIST_LOADING = "product_list_loading"
    const val PRODUCT_LIST_LIST = "product_list_list"
    fun productListItem(productId: String) = "product_list_item_$productId"
    fun productListCategory(category: String?) = "product_list_category_${category ?: "all"}"
    fun productListSortOption(sortOption: SortOption) =
        "product_list_sort_${sortOption.name.lowercase()}"

    //CART SCREEN
    const val CART_LOADING = "cart_loading"
    const val CART_RETRY = "cart_retry"
    const val CART_EMPTY = "cart_empty"

    /*const val CART_LIST = "cart_list"

    const val CART_ERROR = "cart_error"
    fun cartItem(productId: String) = "cart_item_$productId"
    fun cartItemIncrease(productId: String) = "cart_item_increase_$productId"
    fun cartItemDecrease(productId: String) = "cart_item_decrease_$productId"
    fun cartItemRemove(productId: String) = "cart_item_remove_$productId"*/
}
