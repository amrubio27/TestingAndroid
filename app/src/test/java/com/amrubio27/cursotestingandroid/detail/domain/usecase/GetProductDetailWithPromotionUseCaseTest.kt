package com.amrubio27.cursotestingandroid.detail.domain.usecase

import com.amrubio27.cursotestingandroid.core.builders.product
import com.amrubio27.cursotestingandroid.core.builders.promotion
import com.amrubio27.cursotestingandroid.core.fakes.FakeProductRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakePromotionRepository
import com.amrubio27.cursotestingandroid.core.fakes.FakeSystemClock
import com.amrubio27.cursotestingandroid.productlist.domain.model.ProductPromotion
import com.amrubio27.cursotestingandroid.productlist.domain.model.PromotionType
import com.amrubio27.cursotestingandroid.productlist.domain.usecase.GetPromotionForProduct
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetProductDetailWithPromotionUseCaseTest {
    private lateinit var productRepository: FakeProductRepository
    private lateinit var promotionRepository: FakePromotionRepository
    private lateinit var clock: FakeSystemClock

    @Before
    fun setup() {
        productRepository = FakeProductRepository()
        promotionRepository = FakePromotionRepository()
        clock = FakeSystemClock().apply { setTime(Instant.parse("2026-04-03T10:00:00Z")) }
    }

    private fun useCase() = GetProductDetailWithPromotionUseCase(
        productRepository,
        promotionRepository,
        GetPromotionForProduct(),
        clock
    )

    // Si viene un producto con una id y una promo para él que me lo devuelva bien
    @Test
    fun `given product and active promotion when invoke then returns product with promotion`() =
        runTest {
            val now = clock.now()
            val productId = "p1"
            val product = product {
                withId(productId)
                withPrice(10.0)
            }
            val promotion = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(20.0)
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(10))
            }

            productRepository.setProducts(listOf(product))
            promotionRepository.setPromotions(listOf(promotion))

            val result = useCase().invoke(product.id).first()

            // No viene null?
            assertNotNull(result)
            // Su id es la que le he pasado?
            assertEquals(productId, result?.product?.id)
            // existe la promo para el que le he pasado?
            assertNotNull(result?.promotion)
            // su promo es la que le puse?
            assertTrue(result?.promotion is ProductPromotion.Percent)
        }

    // Si no hay promo me devuelve el producto normal sin promo
    @Test
    fun `given existing product and no promotions when invoke then emits product with null promotion`() =
        runTest {
            val productId = "p1"
            val p1 = product { withId(productId); withPrice(100.0) }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(emptyList())

            val result = useCase()(productId).first()

            assertEquals(p1, result?.product)
            assertNull(result?.promotion)
        }

    // Si hay dos promos, una caducada y otra bien, me aseguro que filtre bien
    @Test
    fun `given active and expired promotions when invoke then only active promotion is applied`() =
        runTest {
            val now = clock.now()
            val productId = "p1"

            val p1 = product {
                withId(productId)
                withPrice(100.0)
            }

            val activePromo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(20.0)
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(10))
            }

            val expiredPromo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(50.0) // mayor descuento, pero caducada
                withStartTime(now.minusSeconds(30))
                withEndTime(now.minusSeconds(1))
            }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(listOf(activePromo, expiredPromo))

            val result = useCase()(productId).first()

            assertNotNull(result)
            assertTrue(result?.promotion is ProductPromotion.Percent)
            assertEquals(20.0, (result?.promotion as ProductPromotion.Percent).percent, 0.001)
        }

    // Le paso un producto con promos expiradas me devuelve un producto normal
    @Test
    fun `given product and expired promotion when invoke then returns product with null promotion`() =
        runTest {
            val now = clock.now()
            val productId = "p1"

            val p1 = product {
                withId(productId)
                withPrice(100.0)
            }

            val expiredPromo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(15.0)
                withStartTime(now.minusSeconds(20))
                withEndTime(now.minusSeconds(5)) // Expiró hace 5 segundos
            }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(listOf(expiredPromo))

            val result = useCase()(productId).first()

            assertNotNull(result)
            assertEquals(productId, result?.product?.id)
            assertNull("La promo es nula porque ya habia acabado", result?.promotion)
        }

    // Si no existe el id del producto que le llega deberia devolver null
    @Test
    fun `given non existent product when invoke then returns null`() = runTest {
        // No añadimos nada a los repositorios porque no lo va a encontrar

        val result = useCase()("p_not_found").first()

        assertNull(result)
    }

    // Si pillas una promo pero se te caduca mientras tanto eres un pring... devuelve null la promo
    @Test
    fun `given promotion when time advances then promotion is no longer applied`() =
        runTest {
            val now = clock.now()
            val productId = "p1"

            val p1 = product {
                withId(productId)
                withPrice(100.0)
            }

            // La promo dura solo 5 segundos más
            val promo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(10.0)
                withStartTime(now.minusSeconds(10))
                withEndTime(now.plusSeconds(5))
            }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(listOf(promo))

            val flow = useCase()(productId)

            // Estado inicial: la promo está activa
            assertNotNull(flow.first()?.promotion)

            // Avanzamos el tiempo 6 segundos (la promo caduca)
            clock.advanceTimeBy(6)

            // Nuevo estado: la promo ya no debería aplicarse
            assertNull(flow.first()?.promotion)
        }

    // Comprobando que los casos en los que se iguealen a Start y End
    @Test
    fun `given current time is exactly promotion start time when invoke then promotion is active`() =
        runTest {
            val now = clock.now()
            val productId = "p1"

            val p1 = product {
                withId(productId)
                withPrice(100.0)
            }

            // La promoción empieza EXACTAMENTE en el now
            val promo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(20.0)
                withStartTime(now)
                withEndTime(now.plusSeconds(10))
            }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(listOf(promo))

            val result = useCase()(productId).first()

            assertNotNull(
                "La promo empieza exactamente en el now",
                result?.promotion
            )
        }

    @Test
    fun `given current time is exactly promotion end time when invoke then promotion is still active`() =
        runTest {
            val now = clock.now()
            val productId = "p1"

            val p1 = product {
                withId(productId)
                withPrice(100.0)
            }

            // La promoción termina EXACTAMENTE en el 'now' actual
            val promo = promotion {
                withProductIds(listOf(productId))
                withType(PromotionType.PERCENT)
                withValue(20.0)
                withStartTime(now.minusSeconds(10))
                withEndTime(now)
            }

            productRepository.setProducts(listOf(p1))
            promotionRepository.setPromotions(listOf(promo))

            val result = useCase()(productId).first()

            assertNotNull(
                "Promotion todavía está activa exactamente en el endTime",
                result?.promotion
            )
        }

}