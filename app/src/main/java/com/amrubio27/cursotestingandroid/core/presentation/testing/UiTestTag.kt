package com.amrubio27.cursotestingandroid.core.presentation.testing

object UiTestTag {
    const val TOP_APP_BAR = "top_app_bar"
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
}