package net.philipheur.food_ordering_system.restaurant_service.dataaccess.repository

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.entity.OrderApprovalOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderApprovalOutboxJpaRepository: JpaRepository<OrderApprovalOutboxEntity, UUID> {
    fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    ): List<OrderApprovalOutboxEntity>

    fun findByTypeAndSagaIdAndApprovalStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus,
    ): OrderApprovalOutboxEntity?

    // 주기적으로 완료 메시지를 삭제해 줘서 스페이스를 절약
    // 클린 스케줄러에서 사용
    fun deleteByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    )
}