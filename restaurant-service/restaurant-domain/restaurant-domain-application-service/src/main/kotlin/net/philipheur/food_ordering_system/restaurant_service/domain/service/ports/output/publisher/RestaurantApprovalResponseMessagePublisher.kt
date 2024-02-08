package net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage

interface RestaurantApprovalResponseMessagePublisher {
    fun publish(
        outboxMessage: OrderApprovalOutboxMessage,
        outboxCallback: (OrderApprovalOutboxMessage, OutboxStatus) -> Unit
    )
}