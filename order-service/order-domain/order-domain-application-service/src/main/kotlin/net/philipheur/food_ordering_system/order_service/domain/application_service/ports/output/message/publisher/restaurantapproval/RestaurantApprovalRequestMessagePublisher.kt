package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.restaurantapproval

import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxStatus

interface RestaurantApprovalRequestMessagePublisher {
    fun publish(
        orderApprovalOutboxMessage: OrderApprovalOutboxMessage,
        outboxCallback: (OrderApprovalOutboxMessage, OutboxStatus) -> Unit
    )
}