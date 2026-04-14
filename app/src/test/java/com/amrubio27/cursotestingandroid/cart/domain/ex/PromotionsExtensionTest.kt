package com.amrubio27.cursotestingandroid.cart.domain.ex

import com.amrubio27.cursotestingandroid.core.builders.promotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.Promotion
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class PromotionsExtensionTest {

    private val now = Instant.parse("2026-04-14T11:00:00Z")

    @Test
    fun givenFuturePromotion_whenActiveAt_thenExclude() {

        //Given
        val futurePromotion = promotion {
            withStartTime(now.plusSeconds(10))
            withEndTime(now.plusSeconds(20))
        }
        val promotions = listOf(futurePromotion)

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(0, result.size)
    }

    @Test
    fun givenExpirePromotion_whenActiveAt_thenExclude() {

        //Given
        val expirePromotion = promotion {
            withStartTime(now.minusSeconds(100))
            withEndTime(now.minusSeconds(20))
        }
        val promotions = listOf(expirePromotion)

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(0, result.size)
    }

    @Test
    fun givenOnGoingPromotion_whenActiveAt_thenInclude() {

        //Given
        val activePromotion = promotion {
            withStartTime(now.minusSeconds(1))
            withEndTime(now.plusSeconds(1))
        }
        val promotions = listOf(activePromotion)

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(1, result.size)
    }

    @Test
    fun givenExactStartTimePromotion_whenActiveAt_thenInclude() {

        //Given
        val futurePromotion = promotion {
            withStartTime(now)
            withEndTime(now.plusSeconds(20))
        }
        val promotions = listOf(futurePromotion)

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(1, result.size)
    }

    @Test
    fun givenExactEndStartTimePromotion_whenActiveAt_thenInclude() {

        //Given
        val futurePromotion = promotion {
            withStartTime(now.minusSeconds(100))
            withEndTime(now)
        }
        val promotions = listOf(futurePromotion)

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(1, result.size)
    }

    @Test
    fun givenEmptyList_whenActiveAt_thenReturnEmpty() {

        //Given
        val promotions = emptyList<Promotion>()

        //When
        val result = promotions.activeAt(now)

        //Then
        assertEquals(0, result.size)
    }


}