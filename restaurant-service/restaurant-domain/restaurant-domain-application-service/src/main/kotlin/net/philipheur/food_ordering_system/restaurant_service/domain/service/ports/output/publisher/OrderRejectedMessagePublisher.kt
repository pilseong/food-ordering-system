package net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderRejectedEvent

interface OrderRejectedMessagePublisher:
    DomainEventPublisher<OrderRejectedEvent>