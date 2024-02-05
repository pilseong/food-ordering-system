package net.philipheur.food_ordering_system.restaurant_service.domain.core

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovalEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovedEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderRejectedEvent

interface RestaurantDomainService {
    fun validateOrder(
        restaurant: Restaurant,
        failureMessages: MutableList<String>,
        orderApprovedEventDomainEventPublisher: DomainEventPublisher<OrderApprovedEvent>,
        orderRejectedEventDomainEventPublisher: DomainEventPublisher<OrderRejectedEvent>
    ): OrderApprovalEvent?
}