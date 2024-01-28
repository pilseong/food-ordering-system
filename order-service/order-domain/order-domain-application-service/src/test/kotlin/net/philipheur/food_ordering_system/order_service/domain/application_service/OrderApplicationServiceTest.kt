package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.DeliveryAddressDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.OrderItemDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.mapper.OrderTypeMapper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service.`interface`.OrderApplicationService
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.CustomerRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.RestaurantRepository
import net.philipheur.food_ordering_system.order_service.domain.core.entity.*
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.function.Executable
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [OrderTestConfiguration::class])
class OrderApplicationServiceTest {

    @Autowired
    private var orderApplicationService: OrderApplicationService? = null

    @Autowired
    var orderTypeMapper: OrderTypeMapper? = null

    @Autowired
    private var orderRepository: OrderRepository? = null

    @Autowired
    private var customerRepository: CustomerRepository? = null

    @Autowired
    private var restaurantRepository: RestaurantRepository? = null

    private var createOrderCommand: CreateOrderCommand? = null
    private var createOrderCommandWrongPrice: CreateOrderCommand? = null
    private var createOrderCommandWrongProductPrice: CreateOrderCommand? = null

    private var restaurantResponse: Restaurant? = null

    private val CUSTOMER_ID = UUID.fromString("1d1a1025-5ac0-441c-807d-f119c3567fe7")
    private val RESTAURANT_ID = UUID.fromString("8a613b22-cf7e-484a-a639-29474c08615c")
    private val PRODUCT_ID = UUID.fromString("659b6a33-388f-4a03-bf7c-25c1cc43cf8c")
    private val ORDER_ID = UUID.fromString("1ba883c2-1521-4d07-b8e5-31695d990c0e")
    private val PRICE = BigDecimal("200.00")

    @BeforeAll
    fun init() {
        createOrderCommand =
            CreateOrderCommand(
                customerId = CUSTOMER_ID,
                restaurantId = RESTAURANT_ID,
                deliveryAddress = DeliveryAddressDto(
                    street = "street_!",
                    postalCode = "1000AB",
                    city = "SeongNam"
                ),
                price = PRICE,
                items = listOf(
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 1,
                        price = BigDecimal("50.00"),
                        subTotal = BigDecimal("50.00")
                    ),
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 3,
                        price = BigDecimal("50.00"),
                        subTotal = BigDecimal("150.00")
                    )
                )
            )

        createOrderCommandWrongPrice =
            CreateOrderCommand(
                customerId = CUSTOMER_ID,
                restaurantId = RESTAURANT_ID,
                deliveryAddress = DeliveryAddressDto(
                    street = "street_!",
                    postalCode = "1000AB",
                    city = "SeongNam"
                ),
                price = BigDecimal("250.00"),
                items = listOf(
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 1,
                        price = BigDecimal("50.00"),
                        subTotal = BigDecimal("50.00")
                    ),
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 3,
                        price = BigDecimal("50.00"),
                        subTotal = BigDecimal("150.00")
                    )
                )
            )

        createOrderCommandWrongProductPrice =
            CreateOrderCommand(
                customerId = CUSTOMER_ID,
                restaurantId = RESTAURANT_ID,
                deliveryAddress = DeliveryAddressDto(
                    street = "street_!",
                    postalCode = "1000AB",
                    city = "SeongNam"
                ),
                price = BigDecimal("210.00"),
                items = listOf(
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 1,
                        price = BigDecimal("60.00"),
                        subTotal = BigDecimal("60.00")
                    ),
                    OrderItemDto(
                        productId = PRODUCT_ID,
                        quantity = 3,
                        price = BigDecimal("50.00"),
                        subTotal = BigDecimal("150.00")
                    )
                )
            )

        val customer = Customer(
            customerId = CustomerId(CUSTOMER_ID)
        )

        restaurantResponse = Restaurant(
            restaurantId = RestaurantId(createOrderCommand!!.restaurantId),
            products = listOf(
                Product(
                    productId = ProductId(PRODUCT_ID),
                    name = "product-1",
                    price = Money(BigDecimal("50.00"))
                ),
                Product(
                    productId = ProductId(PRODUCT_ID),
                    name = "product-2",
                    price = Money(BigDecimal("50.00"))
                )
            ),
            active = true
        )

        val order = orderTypeMapper!!
            .crateOrderCommandToOrder(createOrderCommand!!)
        order.id = OrderId(ORDER_ID)

        Mockito.`when`(customerRepository!!.findCustomer(CUSTOMER_ID))
            .thenReturn(customer)

        Mockito.`when`(
            restaurantRepository!!.fetchRestaurantInformation(
                orderTypeMapper!!.createOrderCommandToRestaurant(createOrderCommand!!)
            )
        )
            .thenReturn(restaurantResponse)

        Mockito.`when`(orderRepository!!.save(any()))
            .thenReturn(order)
    }

    @Test
    fun testCreateOrder() {
        val createOrderResponse = orderApplicationService!!
            .createOrder(createOrderCommand!!)

        assertEquals(
            OrderStatus.PENDING,
            createOrderResponse.orderStatus
        )
        assertEquals(
            "Order Created Successfully",
            createOrderResponse.message
        )

        Assertions.assertNotNull(createOrderResponse.orderTrackingId)
    }

    @Test
    fun testCreateOrderWithWrongProductPrice() {
        val orderDomainException = Assertions.assertThrows(
            OrderDomainException::class.java,
            Executable {
                orderApplicationService!!.createOrder(
                    createOrderCommandWrongProductPrice!!
                )
            })

        assertEquals(
            "Order item price: 60.00 is not valid for product $PRODUCT_ID",
            orderDomainException.message
        )
    }

    @Test
    fun testCreatedOrderWithPassiveRestaurant() {
        restaurantResponse!!.active = false


        Mockito.`when`(
            restaurantRepository!!.fetchRestaurantInformation(
                orderTypeMapper!!.createOrderCommandToRestaurant(
                    createOrderCommand!!
                )
            )
        )
            .thenReturn(restaurantResponse)

        val orderDomainException = Assertions.assertThrows(
            OrderDomainException::class.java,
            Executable {
                orderApplicationService!!
                    .createOrder(createOrderCommand!!)
            })

        assertEquals(
            "Restaurant with id " + RESTAURANT_ID +
                    " is currently unavailable",
            orderDomainException.message
        )
    }

}