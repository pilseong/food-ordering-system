package net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import java.util.*

interface OrderOutboxRepository {
    fun save(
        orderOutboxMessage: OrderOutboxMessage
    ): OrderOutboxMessage


    fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    ): List<OrderOutboxMessage>

    fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
    ): OrderOutboxMessage?

    fun deleteByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    )
}