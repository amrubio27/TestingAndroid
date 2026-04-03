package com.amrubio27.cursotestingandroid.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.amrubio27.cursotestingandroid.detail.presentation.ProductDetailScreen
import com.amrubio27.cursotestingandroid.productlist.presentation.ProductListScreen
import com.amrubio27.cursotestingandroid.settings.presentation.SettingsScreen

@Composable
fun NavGraph() {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(Screen.ProductList)
    val entries: (NavKey) -> NavEntry<NavKey> = entryProvider<NavKey> {
        entry<Screen.ProductList> {
            ProductListScreen(
                navigateToSettings = { backStack.add(Screen.Setting) },
                navigateToProductDetail = { productId ->
                    backStack.add(
                        Screen.ProductDetail(
                            productId
                        )
                    )
                }
            )
        }
        entry<Screen.Cart> {
            Text("Cart", fontSize = 30.sp)
        }
        entry<Screen.Setting> {
            SettingsScreen(onBack = { backStack.removeLastOrNull() })
        }
        entry<Screen.ProductDetail> { route ->
            ProductDetailScreen(
                productId = route.productId,
                onBack = { backStack.removeLastOrNull() }
            )
        }
    }
    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() }
    )
}