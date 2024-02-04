package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.entity

import jakarta.persistence.*
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "restaurant_approval_outbox")
class ApprovalOutboxEntity(
    @Id
    val id: UUID,

    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime,
    val type: String,
    val payload: String,

    @Enumerated(EnumType.STRING)
    val sagaStatus: SagaStatus,
    @Enumerated(EnumType.STRING)
    val orderStatus: OrderStatus,
    @Enumerated(EnumType.STRING)
    val outboxStatus: OutboxStatus,

    @Version
    val version: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApprovalOutboxEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}