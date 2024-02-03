package net.philipheur.food_ordering_system.order_service.domain.core

import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCancelledEvent
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderPaidEvent

interface OrderDomainService {

    // 주문 객체를 검증하고 초기화 한다.
    fun validateAndInitiateOrder(
        order: Order,
        restaurant: Restaurant,
    ): OrderCreatedEvent

    fun payOrder(order: Order): OrderPaidEvent

    fun cancelOrder(
        order: Order,
        failureMessages: List<String>
    )

    fun approveOrder(order: Order)

    fun cancelOrderPayment(
        order: Order,
        failureMessages: List<String>,
    ): OrderCancelledEvent
}