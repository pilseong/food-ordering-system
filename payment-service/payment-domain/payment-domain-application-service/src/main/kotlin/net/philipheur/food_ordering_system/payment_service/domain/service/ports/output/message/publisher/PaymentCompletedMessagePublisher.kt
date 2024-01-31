package net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCompletedEvent

interface PaymentCompletedMessagePublisher :
    DomainEventPublisher<PaymentCompletedEvent>