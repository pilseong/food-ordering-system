package net.philipheur.food_ordering_system.order_service.domain.core

import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent

interface OrderDomainService {

    // 주문 객체를 검증하고 초기화 한다.
    fun validateAndInitializeOrder(
        order: Order,
        restaurant: Restaurant
    ): OrderCreatedEvent


//    fun payOrder(order: Order?): OrderPaidEvent?
//    fun approveOrder(order: Order?)
//    fun cancelOrderPayment(order: Order?, failureMessages: List<String?>?): OrderCancelledEvent?
//    fun cancelOrder(order: Order?, failureMessages: List<String?>?)
}