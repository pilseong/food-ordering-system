package net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.entity

import jakarta.persistence.*
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "order_outbox")
class OrderOutboxEntity(
    @Id
    val id: UUID,

    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime?,
    val type: String,
    val payload: String,

    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus,
    @Enumerated(EnumType.STRING)
    val outboxStatus: OutboxStatus,

    @Version
    val version: Int

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderOutboxEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

