package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent


interface OrderCreatedPaymentRequestMessagePublisher :
    DomainEventPublisher<OrderCreatedEvent>