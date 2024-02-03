package net.philipheur.food_ordering_system.order_service.domain.core

import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCancelledEvent
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderPaidEvent
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import java.time.ZoneId
import java.time.ZonedDateTime


class OrderDomainServiceImpl : OrderDomainService {
    private val log by LoggerDelegator()

    // 주문 제품의 정보는 productId만 설정되어 있다.
    override fun validateAndInitiateOrder(
        order: Order,
        restaurant: Restaurant,
    ): OrderCreatedEvent {

        if (!restaurant.active!!) {
            throw OrderDomainException(
                "Restaurant with id " + restaurant.id!!.value.toString() +
                        " is currently unavailable"
            )
        }

        // 주문 제품과 식당의 제품이 동일한지 확인 후 같으면 메뉴이름과 가격이름을 저장한다.
        // 제품 정보를 식당 객체에서 가지고 와서 저장한다.
        setProductInformationFromRestaurant(order, restaurant)

        // 주문의 id와 상태가 null 인지 확인
        order.validateOrder()

        // 주문 id, 상태설정, 배송 id를 할당한다.
        order.initializeOrder()

        log.info("Order with id: {} is initialized", order.id!!.value)

        return OrderCreatedEvent(
            order = order,
            createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
        )
    }

    override fun payOrder(
        order: Order,
    ): OrderPaidEvent {
        order.pay()
        log.info("Order with id: ${order.id} is paid")

        return OrderPaidEvent(
            order = order,
            createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
        )
    }

    override fun cancelOrder(order: Order, failureMessages: List<String>) {
        order.cancel(failureMessages)
        log.info("Order with id: ${order.id} is cancelled")
    }

    override fun approveOrder(order: Order) {
        order.approve()
        log.info("Order with id: ${order.id} is approved")
    }

    override fun cancelOrderPayment(
        order: Order,
        failureMessages: List<String>,
    ): OrderCancelledEvent {
        order.initCancel(failureMessages)
        log.info("Order payment is cancelling for order id: ${order.id!!.value}");
        return OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)))
    }



//--------------------------------------------------------------------------
//    private section

    // 주문 제품과 식당의 제품이 동일한지 ProductId를 비교 후 같으면
    // 식당의 메뉴이름과 가격이름을 주문 제품 정보등록한다.
    // productId 비교 같은 경우 -> 식당 product 정보를 order item 의 product 에 저장
    private fun setProductInformationFromRestaurant(
        order: Order,
        restaurant: Restaurant
    ) {
        order.items.forEach { item ->
            restaurant.products.forEach { restaurantProduct ->
                val orderedProduct = item.product
                if (orderedProduct == restaurantProduct) {
                    orderedProduct.name = restaurantProduct.name
                    orderedProduct.price = restaurantProduct.price
                }
            }
        }
    }
}