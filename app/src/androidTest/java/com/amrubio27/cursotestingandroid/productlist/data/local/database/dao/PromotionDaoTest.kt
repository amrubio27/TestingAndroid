package com.amrubio27.cursotestingandroid.productlist.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amrubio27.cursotestingandroid.core.builder.promotionEntity
import com.amrubio27.cursotestingandroid.core.data.local.database.MiniMarketDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PromotionDaoTest {

    private lateinit var database: MiniMarketDatabase

    private lateinit var dao: PromotionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MiniMarketDatabase::class.java
        ).build()
        dao = database.promotionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenEmptyDatabase_whenGetAllPromotions_thenEmitsEmptyList() = runTest {
        val result = dao.getAllPromotions().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun givenPromotionsList_whenInsertPromotions_thenDataIsPersisted() = runTest {
        val promotions = listOf(
            promotionEntity { withId("1") },
            promotionEntity { withId("2") }
        )
        dao.insertPromotions(promotions)

        val result = dao.getAllPromotions().first()

        assertEquals(2, result.size)
        assertEquals(promotions, result)
    }

    @Test
    fun givenExistingPromotion_whenInsertSameId_thenPromotionIsReplaced() = runTest {
        val id = "1"
        val initialPromotion = promotionEntity { withId(id); withType("PERCENT") }
        val updatedPromotion = promotionEntity { withId(id); withType("BUY_X_PAY_Y") }

        dao.insertPromotions(listOf(initialPromotion))
        dao.insertPromotions(listOf(updatedPromotion))

        val result = dao.getAllPromotions().first()

        assertEquals(1, result.size)
        assertEquals("BUY_X_PAY_Y", result.first().type)
    }

    @Test
    fun givenExistingData_whenReplaceAll_thenOldDataIsClearedAndNewDataInserted() = runTest {
        val oldPromotions = listOf(promotionEntity { withId("OLD") })
        val newPromotions = listOf(promotionEntity { withId("NEW") })

        dao.insertPromotions(oldPromotions)
        dao.replaceAll(newPromotions)

        val result = dao.getAllPromotions().first()

        assertEquals(1, result.size)
        assertEquals("NEW", result.first().id)
    }

}