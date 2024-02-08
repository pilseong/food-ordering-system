package net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.repository

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.entity.OrderOutboxEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderOutboxJpaRepository: JpaRepository<OrderOutboxEntity, UUID> {

    fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    ): List<OrderOutboxEntity>

    fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
    ): OrderOutboxEntity?

    // 주기적으로 완료 메시지를 삭제해 줘서 스페이스를 절약
    // 클린 스케줄러에서 사용
    fun deleteByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus,
    )
}