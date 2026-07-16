package com.amrubio27.cursotestingandroid.core

import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import com.amrubio27.cursotestingandroid.core.domain.model.AppError
import com.amrubio27.cursotestingandroid.core.mockwebserver.MiniMarketApiDispatcher
import com.amrubio27.cursotestingandroid.core.mockwebserver.MockWebServerUrlHolder
import com.amrubio27.cursotestingandroid.core.mockwebserver.ProductErrorDispatcher
import com.amrubio27.cursotestingandroid.core.mockwebserver.rules.MockWebServerRule
import com.amrubio27.cursotestingandroid.core.utils.asAsset
import com.amrubio27.cursotestingandroid.productlist.domain.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFailsWith

@HiltAndroidTest
class OfflineFirstIntegrationTest {
    companion object {
        const val DEFAULT_PRODUCT_ASSET = "products_list_default.json"
        const val UPDATE_PRODUCT_ASSET = "products_list_updated.json"
        const val DEFAULT_PRODUCT_SIZE = 3
        const val UPDATE_PRODUCT_SIZE = 1
    }

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var db: MiniMarketDatabase

    @Inject
    lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        hilt.inject()
        db.clearAllTables()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenSuccessfulRefresh_whenGetProducts_thenRoomContainsRemoteProducts() =
        runTest {
            serveProductsFromAsset(DEFAULT_PRODUCT_ASSET)

            productRepository.refreshProducts()

            val cachedProducts =
                productRepository.getProducts().first { products ->
                    products.size == DEFAULT_PRODUCT_SIZE
                }

            assertEquals(DEFAULT_PRODUCT_SIZE, cachedProducts.size)
        }

    @Test
    fun givenEmptyCacheAndFailedRefresh_whenGetProducts_thenEmitsEmptyList() =
        runTest {
            serveProductsError()

        /*val result: Result<Unit> = runCatching { productRepository.refreshProducts() }
        assertTrue(result.isFailure)*/

            assertFailsWith<AppError.NetworkError> {
                productRepository.refreshProducts()
            }

            val products = productRepository.getProducts().first { it.isEmpty() }

            assertTrue(products.isEmpty())
        }

    @Test
    fun givenCachedProductsAndFailedRefresh_whenGetProducts_thenReturnsPreviousCache() =
        runTest {
            serveProductsFromAsset(DEFAULT_PRODUCT_ASSET)
            productRepository.refreshProducts()
            productRepository.getProducts().first { products ->
                products.size == DEFAULT_PRODUCT_SIZE
            }

            serveProductsError()
            assertFailsWith<AppError.NetworkError> {
                productRepository.refreshProducts()
            }

            val cachedProducts =
                productRepository.getProducts().first { products ->
                    products.size == DEFAULT_PRODUCT_SIZE
                }

            assertEquals(DEFAULT_PRODUCT_SIZE, cachedProducts.size)
        }

    @Test
    fun givenCachedProducts_whenRefreshWithNewPayload_thenContainsOnlyLatestProducts() =
        runTest {
            serveProductsFromAsset(DEFAULT_PRODUCT_ASSET)
            productRepository.refreshProducts()
            productRepository.getProducts().first { products ->
                products.size == DEFAULT_PRODUCT_SIZE
            }

            serveProductsFromAsset(UPDATE_PRODUCT_ASSET)
            productRepository.refreshProducts()

            val updatedProducts =
                productRepository.getProducts().first {
                    it.size == UPDATE_PRODUCT_SIZE
                }

            assertEquals(UPDATE_PRODUCT_SIZE, updatedProducts.size)
            assertEquals("updated-p1", updatedProducts.first().id)
            assertEquals("Pan integral", updatedProducts.first().name)
        }

    private fun serveProductsFromAsset(assetName: String) {
        mockWebServer.server.dispatcher =
            MiniMarketApiDispatcher(
                productJson = assetName.asAsset(),
            )
    }

    private fun serveProductsError() {
        mockWebServer.server.dispatcher = ProductErrorDispatcher()
    }
}