package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import java.time.ZonedDateTime
import java.util.*

data class OrderApprovalOutboxMessage(
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String,
    val payload: String,
    val orderApprovalStatus: OrderApprovalStatus,
    val outboxStatus: OutboxStatus,
    val version: Int = 0,
)