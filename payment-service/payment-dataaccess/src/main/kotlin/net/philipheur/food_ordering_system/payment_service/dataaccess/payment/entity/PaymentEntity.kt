package net.philipheur.food_ordering_system.payment_service.dataaccess.payment.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import java.math.BigDecimal
import java.time.ZonedDateTime

import java.util.*

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    var id: UUID,

    var customerId: UUID,
    var orderId: UUID,
    var price: BigDecimal,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    var createdAt: ZonedDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}