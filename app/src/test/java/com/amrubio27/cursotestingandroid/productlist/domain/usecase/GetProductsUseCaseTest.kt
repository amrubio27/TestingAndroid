package com.amrubio27.cursotestingandroid.productlist.domain.usecase

import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.builders.promotion
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakePromotionRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeSettingsRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeSystemClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class GetProductsUseCaseTest {
    private fun useCase(
        products: FakeProductRepository = FakeProductRepository(),
        promos: FakePromotionRepository = FakePromotionRepository(),
        settings: FakeSettingsRepository = FakeSettingsRepository(),
        clock: FakeSystemClock = FakeSystemClock()

    ) = GetProductsUseCase(products, promos, GetPromotionForProduct(), settings, clock)

    @Test
    fun `given promotion ending now when invoke then it should be included`() = runTest {
        //given
        val now = Instant.parse("2026-06-01T12:00:00Z")
        val clock = FakeSystemClock().apply {
            setTime(now)
        }
        val productId = "product-id"
        val product = product {
            withId(productId)
        }
        val promo = promotion {
            withProductIds(listOf(productId))
            withStartTime(now.minusSeconds(60))
            withEndTime(now)
        }

        val productRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val promotionRepository = FakePromotionRepository().apply {
            setPromotions(listOf(promo))
        }

        //when
        val result = useCase(
            products = productRepository,
            promos = promotionRepository,
            clock = clock
        ).invoke().first()

        //then
        assertNotNull(result.first())
    }

    @Test
    fun `given active promotion when time advances then promotion should not be longer be returned`() =
        runTest {
            //given
            val now = Instant.parse("2026-04-03T10:00:00Z")
            val clock = FakeSystemClock().apply {
                setTime(now)
            }
            val productId = "product-id"
            val product = product {
                withId(productId)
            }
            val promo = promotion {
                withProductIds(listOf(productId))
                withStartTime(now.minusSeconds(1))
                withEndTime(now.plusSeconds(5))
            }

            val productRepository = FakeProductRepository().apply {
                setProducts(listOf(product))
            }
            val promotionRepository = FakePromotionRepository().apply {
                setPromotions(listOf(promo))
            }

            //when
            val firstResult = useCase(
                products = productRepository,
                promos = promotionRepository,
                clock = clock
            ).invoke().first()

            clock.advanceTimeBy(6)

            val secondResult = useCase(
                products = productRepository,
                promos = promotionRepository,
                clock = clock
            ).invoke().first()

            //then
            assertNotNull(firstResult.first().promotion)
            assertNull(secondResult.first().promotion)
        }

    @Test
    fun `given inStock Only enabled when product goes out of stock then it should be filtered`() =
        runTest {
            val productId = "product-id"
            val product = product {
                withId(productId)
                withStock(0)
            }

            val settings = FakeSettingsRepository().apply {
                setInStockOnly(true)
            }

            val productRepository = FakeProductRepository().apply {
                setProducts(listOf(product))
            }

            val myUseCase = useCase(
                products = productRepository,
                settings = settings
            )

            //when
            val result = myUseCase.invoke().first()

            //then
            assertTrue(result.isEmpty())

        }
}