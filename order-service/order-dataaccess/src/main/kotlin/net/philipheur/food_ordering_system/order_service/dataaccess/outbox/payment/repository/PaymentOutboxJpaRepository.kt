package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.repository

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
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

    // 주기적으로 완료 메시지를 삭제해 줘서 스페이스를 절약
    // 클린 스케줄러에서 사용
    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    )
}