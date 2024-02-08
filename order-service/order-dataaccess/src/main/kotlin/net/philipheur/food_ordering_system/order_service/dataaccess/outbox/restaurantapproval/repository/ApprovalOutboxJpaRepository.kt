package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.repository

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ApprovalOutboxJpaRepository: JpaRepository<ApprovalOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    ): List<ApprovalOutboxEntity>?

    fun findByTypeAndSagaIdAndSagaStatusIn(
        type: String,
        sagaId: UUID,
        sagaStatus: List<SagaStatus>
    ): ApprovalOutboxEntity?

    // 주기적으로 완료 메시지를 삭제해 줘서 스페이스를 절약
    // 클린 스케줄러에서 사용
    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    )
}