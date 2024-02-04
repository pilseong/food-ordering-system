package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertTrue

class OrderItemTest {
    @BeforeEach
    fun setUp() {
    }

    @Test
    fun isPriceValid() {

        val product1Id = ProductId(UUID.randomUUID())

        val product1 = Product(
            productId = product1Id,
            price = Money(BigDecimal("50.00"))
        )

        val beforeInitOrderItem = OrderItem(
            product = product1,
            quantity = 2,
            price = Money(BigDecimal("50.00")),
            subTotal = Money(BigDecimal("100.00")),
        )

        assertTrue(beforeInitOrderItem.price.isGreaterThanZero())
        assertTrue {
            beforeInitOrderItem.price == beforeInitOrderItem.product.price
        }
        assertTrue(beforeInitOrderItem.isPriceValid())
    }
}