package net.philipheur.food_ordering_system.order_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.AggregateRoot
import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderItemId
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.OrderAddress
import net.philipheur.food_ordering_system.order_service.domain.core.valueobject.TrackingId
import java.util.*


class Order(
    orderId: OrderId? = null,
    val restaurantId: RestaurantId,
    val customerId: CustomerId,
    val orderAddress: OrderAddress,
    val items: List<OrderItem>,
    val price: Money,
    var trackingId: TrackingId? = null,
    var orderStatus: OrderStatus? = null,
    var failureMessage: List<String>? = null
) : AggregateRoot<OrderId>(orderId) {

    // 주문 검증
    fun validateOrder() {
        // 주문의 초기 객체 상태 확인
        checkDefaultState()

        // 주문에 포함된 메뉴들의 가격이 정확한지 확인
        validatePrice()
    }

    // 1. 주문 객체 초기화 - 주문 id, 주문상태 설정, 배송 id 생성
    // 2. 주문 내에 제품 객체들 정보 설정
    fun initializeOrder() {
        id = OrderId(UUID.randomUUID())
        trackingId = TrackingId(UUID.randomUUID())
        orderStatus = OrderStatus.PENDING

        // 각 주문 메뉴들도 초기화
        initializeOrderItems()
    }

    // 주문 객체가 생성되면 id와 주문 상태는 null 이어야 한다.
    private fun checkDefaultState() {
        if (orderStatus != null || id != null) {
            throw OrderDomainException(
                "Order is not in correct state for initialization"
            )
        }
    }

    // 1. 주문 내의 order item 검증
    // 2. 주문 전체 price 와 order item 의 합계를 검증
    private fun validatePrice() {
        // 고객의 주문 전체가 0보다 커야 한다.
        validatePriceGreaterThanZero()

        // 주문 내의 order items 검증
        items.forEach {
            validateOrderItemPrice(it)
        }

        // 주문으로 들어와 있는 주문 목록의 합을 구한다.
        val orderItemsTotal: Money =
            items
                .map { it.subTotal }
                .reduce { acc, money -> acc.add(money) }

        // 주문의 price 가 구성하는 각 item 의 가격의 합과 동일한지를 검증
        if (price != orderItemsTotal) {
            throw OrderDomainException(
                "Total price: ${price.amount} " +
                        "is not equal to " +
                        "Order items total: ${orderItemsTotal.amount}"
            )
        }
    }

    // 주문 내의 order item 검증
    private fun validateOrderItemPrice(it: OrderItem) {
        if (!it.isPriceValid()) {
            throw OrderDomainException(
                "Order item price: ${it.price.amount} " +
                        "is not valid " +
                        "for product ${it.product.id!!.value}"
            )
        }
    }

    private fun validatePriceGreaterThanZero() {
        if (!price.isGreaterThanZero()) {
            throw OrderDomainException(
                "Total price must be greater than zero"
            )
        }
    }

    private fun initializeOrderItems() {
        var itemId = 1L
        for (orderItem in items) {
            orderItem.initializeOrderItem(
                orderId = super.id!!,
                orderItemId =  OrderItemId(itemId++)
            )
        }
    }
}