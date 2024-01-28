package net.philipheur.food_ordering_system.order_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import java.time.ZonedDateTime

class OrderCreatedEvent(
    order: Order,
    createdAt: ZonedDateTime
) : OrderEvent(order, createdAt) {
}