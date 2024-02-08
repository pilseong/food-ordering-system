package net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage

interface PaymentResponseMessagePublisher {
    fun publish(
        orderOutboxMessage: OrderOutboxMessage,
        outboxCallback: (OrderOutboxMessage, OutboxStatus) -> Unit
    )
}