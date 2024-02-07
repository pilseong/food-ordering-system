package net.philipheur.food_ordering_system.restaurant_service.domain.core

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovalEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovedEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderRejectedEvent
import java.time.ZoneId
import java.time.ZonedDateTime


class RestaurantDomainServiceImpl : RestaurantDomainService {

    private val log by LoggerDelegator()
    override fun validateOrder(
        restaurant: Restaurant,
        failureMessages: MutableList<String>,
        orderApprovedEventDomainEventPublisher: DomainEventPublisher<OrderApprovedEvent>,
        orderRejectedEventDomainEventPublisher: DomainEventPublisher<OrderRejectedEvent>
    ): OrderApprovalEvent {

        // 주문의 상태와 주문의 가격을 검증한다.
        restaurant.validateOrder(failureMessages)

        log.info("Validating order with id: ${restaurant.orderDetail.id!!.value}")

        // 에러메시지가 없다는 것은 정상처리 되었다는 의미
        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: ${restaurant.orderDetail.id!!.value}")

            // OrderApproval 객체를 생성하고 요청응답을 승인으로 한다.
            restaurant.createOrderApproval(OrderApprovalStatus.APPROVED)
            return OrderApprovedEvent(
                orderApproval = restaurant.orderApproval!!,
                restaurantId = restaurant.id!!,
                failureMessage = failureMessages,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher
            )
        } else {
            log.info("Order is rejected for order id: ${restaurant.orderDetail.id!!.value}")

            // OrderApproval 객체를 생성하고 요청에 거절로 설정
            restaurant.createOrderApproval(OrderApprovalStatus.REJECTED)
            return OrderRejectedEvent(
                orderApproval = restaurant.orderApproval!!,
                restaurantId = restaurant.id!!,
                failureMessage = failureMessages,
                createdAt = ZonedDateTime.now(ZoneId.of(UTC)),
                orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher
            )
        }
    }
}