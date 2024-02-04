package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.repository

import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
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

    fun deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type: String,
        outboxStatus: OutboxStatus,
        sagaStatus: List<SagaStatus>
    )
}