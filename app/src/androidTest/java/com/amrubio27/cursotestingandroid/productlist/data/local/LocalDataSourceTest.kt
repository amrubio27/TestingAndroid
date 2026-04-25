package com.amrubio27.cursotestingandroid.productlist.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amrubio27.cursotestingandroid.core.builder.cartItemEntity
import com.amrubio27.cursotestingandroid.core.builder.productEntity
import com.amrubio27.cursotestingandroid.core.builder.promotionEntity
import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: MiniMarketDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), MiniMarketDatabase::class.java
        ).build()
        localDataSource = LocalDataSource(
            database.productDao(),
            database.promotionDao(),
            database.cartItemDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenProducts_whenSaveAndGetAll_thenReturnsPersistedProduct() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("leche") },
            productEntity { withId("2") }
        )

        localDataSource.saveProducts(products)
        val result = localDataSource.getAllProducts().first()

        assertEquals(2, result.size)
    }

    @Test
    fun givenSavedProduct_whenGetProductById_thenReturnsCorrectProduct() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("leche") },
            productEntity { withId("2") }
        )

        localDataSource.saveProducts(products)
        val result = localDataSource.getProductById("1").first()

        assertNotNull(result)
        assertEquals("leche", result?.name)
    }

    @Test
    fun givenThreeProducts_whenGetProductsById_thenReturnsRequestedSubset() = runTest {
        val products = listOf(
            productEntity { withId("1"); withName("leche") },
            productEntity { withId("2"); withName("carne") },
            productEntity { withId("3"); withName("cookies") },
        )

        localDataSource.saveProducts(products)

        val result = localDataSource.getProductsByIds(setOf("1", "3")).first()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "leche" })
        assertTrue(result.any { it.name == "cookies" })
    }

    @Test
    fun givenPromotions_whenSaveAndGetAll_thenReturnsPersistedPromotions() = runTest {
        val promotions = listOf(
            promotionEntity { withId("id1") },
            promotionEntity { withId("id2"); withProductIds("""["productId1"]""") }
        )

        localDataSource.savePromotions(promotions)

        val result = localDataSource.getAllPromotions().first()

        assertEquals(2, result.size)
    }

    @Test
    fun givenCartItem_whenInsertCartItem_thenReturnsSuccessAndItemSaved() = runTest {
        val cartItem = cartItemEntity { withProductId("id1"); withQuantity(2) }

        val result: Result<Unit> = localDataSource.insertCartItem(itemEntity = cartItem)
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()
        assertTrue(1 == items.size)
        assertEquals("id1", items.first().productId)
    }

    @Test
    fun givenExistingItem_whenUpdateCartItem_thenReturnsSuccessAndCartItemUpdated() = runTest {
        val cartItem = cartItemEntity { withProductId("id1"); withQuantity(2) }
        localDataSource.insertCartItem(itemEntity = cartItem)

        val cartItem2 = cartItemEntity { withProductId("id1"); withQuantity(67) }
        val result: Result<Unit> = localDataSource.updateCartItem(cartItemEntity = cartItem2)
        assertTrue(result.isSuccess)

        val item = localDataSource.getCartItemById(productId = "id1")
        assertNotNull(item)
        assertEquals(67, item?.quantity)
    }

    @Test
    fun givenCartItem_whenDeleteCartItem_thenReturnsSuccessAndCartIsEmpty() = runTest {
        val cartItem = cartItemEntity { withProductId("id1"); withQuantity(2) }
        localDataSource.insertCartItem(itemEntity = cartItem)

        val result: Result<Unit> = localDataSource.deleteCartItem(cartItemEntity = cartItem)
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun givenMultipleCartItem_whenClearCart_thenReturnsSuccessAndCartIsEmpty() = runTest {
        val cartItem = cartItemEntity { withProductId("id1"); withQuantity(2) }
        val cartItem2 = cartItemEntity { withProductId("id2"); withQuantity(2) }
        val cartItem3 = cartItemEntity { withProductId("id3"); withQuantity(2) }

        localDataSource.insertCartItem(itemEntity = cartItem)
        localDataSource.insertCartItem(itemEntity = cartItem2)
        localDataSource.insertCartItem(itemEntity = cartItem3)

        val result: Result<Unit> = localDataSource.clearCart()
        assertTrue(result.isSuccess)

        val items = localDataSource.getAllCartItems().first()
        assertTrue(items.isEmpty())
    }


}