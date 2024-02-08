package net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import java.util.*

interface OrderApprovalOutboxRepository {
    fun save(
        orderOutboxMessage: OrderApprovalOutboxMessage
    ): OrderApprovalOutboxMessage


    fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    ): List<OrderApprovalOutboxMessage>

    fun findByTypeAndSagaIdAndOrderApprovalStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus,
    ): OrderApprovalOutboxMessage?

    fun deleteByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    )
}