package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.repository

import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.entity.CreditHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

import java.util.*

interface CreditHistoryJpaRepository: JpaRepository<CreditHistoryEntity, UUID> {

    fun findByCustomerId(customerId: UUID): List<CreditHistoryEntity>
}