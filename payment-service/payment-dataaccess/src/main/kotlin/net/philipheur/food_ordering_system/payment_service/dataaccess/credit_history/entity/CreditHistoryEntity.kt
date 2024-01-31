package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.entity

import jakarta.persistence.*
import net.philipheur.food_ordering_system.payment_service.domain.core.valueobject.TransactionType
import java.math.BigDecimal

import java.util.*
@Entity
@Table(name = "credit_history")
class CreditHistoryEntity(
    @Id
    val id: UUID,
    val customerId: UUID,

    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    val type: TransactionType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreditHistoryEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}