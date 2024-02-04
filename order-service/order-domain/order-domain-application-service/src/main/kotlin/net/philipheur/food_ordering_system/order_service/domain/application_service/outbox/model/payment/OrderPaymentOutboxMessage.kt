package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment

import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

data class OrderPaymentOutboxMessage(
    val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    var processedAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of(UTC)),
    val type: String,
    val payload: String,
    val sagaStatus: SagaStatus,
    val orderStatus: OrderStatus,
    val outboxStatus: OutboxStatus,
    val version: Int = 0,
)