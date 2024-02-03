package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment

import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxStatus

interface PaymentRequestMessagePublisher {
    fun publish(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
        outboxCallback: (OrderPaymentOutboxMessage, OutboxStatus) -> Unit
    )
}