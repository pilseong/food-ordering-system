package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.repository

import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentOutboxJpaRepository: JpaRepository<PaymentOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    ): List<PaymentOutboxEntity>?

    fun findByTypeAndSagaIdAndSagaStatusIn(
        type: String,
        sagaId: UUID,
        sagaStatus: List<SagaStatus>
    ): PaymentOutboxEntity?

    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    )
}