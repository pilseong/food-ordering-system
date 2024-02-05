package net.philipheur.food_ordering_system.restaurant_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantId
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderApproval
import java.time.ZonedDateTime

class OrderApprovedEvent(
    orderApproval: OrderApproval,
    restaurantId: RestaurantId,
    failureMessage: MutableList<String>,
    createdAt: ZonedDateTime,
    private val orderApprovedEventDomainEventPublisher:
    DomainEventPublisher<OrderApprovedEvent>
) : OrderApprovalEvent(
    orderApproval,
    restaurantId,
    failureMessage,
    createdAt
) {
    override fun fire() {
        orderApprovedEventDomainEventPublisher.publish(this)
    }
}