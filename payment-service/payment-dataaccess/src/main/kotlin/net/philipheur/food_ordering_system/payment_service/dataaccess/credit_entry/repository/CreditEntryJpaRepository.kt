package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.repository

import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.entity.CreditEntryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CreditEntryJpaRepository
    : JpaRepository<CreditEntryEntity, UUID> {

    fun findByCustomerId(customerId: UUID): CreditEntryEntity?
}