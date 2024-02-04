package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository

import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus

import java.util.*

interface PaymentOutboxRepository {
    fun save(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage
    ): OrderPaymentOutboxMessage


    fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderPaymentOutboxMessage>

    fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ): OrderPaymentOutboxMessage?

    fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    )
}