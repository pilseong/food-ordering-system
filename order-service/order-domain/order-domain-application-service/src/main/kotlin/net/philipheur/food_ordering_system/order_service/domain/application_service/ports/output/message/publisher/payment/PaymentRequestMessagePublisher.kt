package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage

interface PaymentRequestMessagePublisher {
    fun publish(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
        outboxCallback: (OrderPaymentOutboxMessage, OutboxStatus) -> Unit
    )
}