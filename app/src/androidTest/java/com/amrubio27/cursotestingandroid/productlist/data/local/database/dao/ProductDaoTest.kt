package com.amrubio27.cursotestingandroid.productlist.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.amrubio27.cursotestingandroid.core.builder.productEntity
import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: MiniMarketDatabase
    private lateinit var dao: ProductDao

    //Se ejecuta antes de los tests y es en el que se instancian las cosas que le demos
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), //contexto del test
            MiniMarketDatabase::class.java
        ).build() //crea una base de datos nueva en memoria para testearla
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenEmptyDataBase_whenGetAllProducts_thenEmitsEmptyList() = runTest {

        val products = dao.getAllProducts().first()
        assertTrue(products.isEmpty())
    }

    @Test
    fun givenInsertedProduct_whenGetProductById_thenReturnsProduct() = runTest {
        val id = "id"
        val p = productEntity { withId(id) }
        dao.insertAllProducts(listOf(p))

        val product = dao.getProductById(id).first()

        assertNotNull(product)
        assertEquals(id, product.id)
    }

    @Test
    fun givenThreeProducts_whenGetProductsByIds_thenReturnsRequestSubset() = runTest {
        dao.insertAllProducts(
            listOf(
                productEntity { withId("1") },
                productEntity { withId("2") },
                productEntity { withId("3") }
            ))

        val products = dao.getProductsByIds(listOf("1", "3")).first()

        assertTrue(products.any { it.id == "1" })
        assertTrue(products.any { it.id == "3" })
        assertTrue(products.none { it.id == "2" })
    }

    @Test
    fun givenOldProducts_whenREplaceAll_thenOnlyNewProductsRemain() = runTest {
        dao.insertAllProducts(
            listOf(
                productEntity { withId("old-1") },
                productEntity { withId("old-2") }
            ))

        val newProducts = listOf(
            productEntity { withId("new-1") },
            productEntity { withId("new-2") },
            productEntity { withId("new-3") }
        )

        dao.replaceAll(newProducts)

        val result = dao.getAllProducts().first()

        assertEquals(newProducts.size, result.size)
        assertTrue(result.none {
            it.id == "old-1" || it.id == "old-2"
        })
        assertTrue(result.any { it.id == "new-1" } && result.any { it.id == "new-2" } && result.any { it.id == "new-3" })
    }

    @Test
    fun givenExistingProduct_whenInsertSameIdWithDifferentData_thenReplaceOldData() = runTest {
        val productId = "id"
        val p1 = productEntity { withId(productId); withName("pan") }
        dao.insertAllProducts(listOf(p1))

        val p2 = productEntity { withId(productId); withName("leche") }
        dao.insertAllProducts(listOf(p2))

        val result = dao.getAllProducts().first()
        assertTrue(result.size == 1)
        assertEquals("leche", result.first().name)
    }

    @Test
    fun givenFlowSubscribed_whenInsertAfterSubscribe_thenEmitsUpdateList() = runTest {
        dao.getAllProducts().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            dao.insertAllProducts(listOf(productEntity { withId("2") }))

            val updated = awaitItem()

            assertEquals(1, updated.size)
            assertEquals("2", updated.first().id)

            cancelAndIgnoreRemainingEvents()

        }
    }
}