package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderAddress
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderItemId
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId
import org.junit.jupiter.api.*
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderTest {

    private val orderId = OrderId(UUID.randomUUID())
    private val restaurantId = RestaurantId(UUID.randomUUID())
    private val customerId = CustomerId(UUID.randomUUID())
    private val orderAddressId: UUID = UUID.randomUUID()
    private val orderAddress = OrderAddress(
        id = orderAddressId,
        street = "good street",
        postalCode = "12345",
        city = "good city"
    )
    private val product1Id = ProductId(UUID.randomUUID())

    private val product1 = Product(
        productId = product1Id,
        price = Money(BigDecimal("50.00"))
    )

    private val orderItem1Id = OrderItemId(1L)
    private val orderItem1 = OrderItem(
        orderId = orderId,
        orderItemId = orderItem1Id,
        product = product1,
        quantity = 1,
        subTotal = Money(BigDecimal("50.00")),
        price = Money(BigDecimal("50.00"))
    )

    private val beforeInitOrderItem = OrderItem(
        product = product1,
        quantity = 1,
        subTotal = Money(BigDecimal("50.00")),
        price = Money(BigDecimal("50.00")),
    )

    private val tracking1Id = TrackingId(UUID.randomUUID())

    private val order = Order(
        orderId = orderId,
        restaurantId = restaurantId,
        customerId = customerId,
        orderAddress = orderAddress,
        items = listOf(orderItem1),
        price = Money(BigDecimal(50.00)),
        trackingId = tracking1Id,
        orderStatus = OrderStatus.PENDING,
        failureMessage = mutableListOf("failure1", "failure2")
    )

    private val beforeInitOrder = Order(
        restaurantId = restaurantId,
        customerId = customerId,
        orderAddress = orderAddress,
        items = listOf(beforeInitOrderItem),
        price = Money(BigDecimal("50.00")),
    )

    @BeforeEach
    fun setUp() {

    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `validateOrder checks id and orderStatus are null and check all the values are correct`() {

        val order1 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(beforeInitOrderItem),
            price = Money(BigDecimal("50.00")),
        )

        val order2 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(beforeInitOrderItem, beforeInitOrderItem),
            price = Money(BigDecimal("100.00")),
        )

        val order3 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(
                beforeInitOrderItem,
                beforeInitOrderItem,
                beforeInitOrderItem
            ),
            price = Money(BigDecimal("100.00")),
        )

        assertNull(order1.id)
        assertNull(order1.orderStatus)

        order1.validateOrder()

        order2.validateOrder()

        assertThrows<OrderDomainException> {
            order3.validateOrder()
        }
    }

    @Test
    fun `initializeOrder creates id, trackingId, orderStatus to PENDING`() {
        val order1 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(beforeInitOrderItem),
            price = Money(BigDecimal("50.00")),
        )
        assertNull(order1.id)
        assertNull(order1.trackingId)
        assertNull(order1.orderStatus)

        order1.items.forEach {
            assertNull(it.id)
            assertNull(it.orderId)
        }

        order1.initializeOrder()

        assertNotNull(order1.id)
        assertNotNull(order1.trackingId)
        assertEquals(OrderStatus.PENDING, order1.orderStatus)

        order1.items.forEach {
            assertNotNull(it.id)
            assertNotNull(it.orderId)
        }
    }

    @Test
    fun `pay makes orderStatus to PAID`() {
        val order1 = Order(
            orderId = orderId,
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            trackingId = tracking1Id,
            orderStatus = OrderStatus.PENDING,
            failureMessage = mutableListOf("failure1", "failure2")
        )

        order1.pay()

        assertEquals(OrderStatus.PAID, order1.orderStatus)

        val order2 = Order(
            orderId = orderId,
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            trackingId = tracking1Id,
            orderStatus = OrderStatus.CANCELLED,
            failureMessage = mutableListOf("failure1", "failure2")
        )

        assertThrows<OrderDomainException> {
            order2.pay()
        }

    }

    @Test
    fun initCancel() {
    }

    @Test
    fun `cancel adds up the failure messages or makes failures list if it does not exists`() {
        val order1 = Order(
            orderId = orderId,
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            trackingId = tracking1Id,
            orderStatus = OrderStatus.PENDING,
            failureMessage = mutableListOf("failure1", "failure2")
        )

        order1.cancel(listOf("test1", "test2"))

        assertEquals(order1.failureMessage!!.size, 4)

        val order2 = Order(
            orderId = orderId,
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            trackingId = tracking1Id,
            orderStatus = OrderStatus.PENDING,
        )

        order2.cancel(listOf("test1", "test2"))
        assertEquals(order2.failureMessage!!.size, 2)

    }

    @Test
    fun `cancel allows only PENDING, OR CANCELLING order status`() {
        val order1 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
        )

        assertThrows<OrderDomainException> {
            order1.cancel(emptyList())
        }

        val order2 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            orderStatus = OrderStatus.CANCELLED
        )
        assertThrows<OrderDomainException> {
            order2.cancel(emptyList())
        }

        val order3 = Order(
            restaurantId = restaurantId,
            customerId = customerId,
            orderAddress = orderAddress,
            items = listOf(orderItem1),
            price = Money(BigDecimal(50.00)),
            orderStatus = OrderStatus.CANCELLING
        )

        order3.cancel(emptyList())

        assertEquals(OrderStatus.CANCELLED, order3.orderStatus)
    }
}