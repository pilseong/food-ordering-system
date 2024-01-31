package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

import java.util.*


@Entity
@Table(name = "credit_entry")
class CreditEntryEntity(
    @Id
    var id: UUID,

    var customerId: UUID,
    var totalCreditAmount: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreditEntryEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}