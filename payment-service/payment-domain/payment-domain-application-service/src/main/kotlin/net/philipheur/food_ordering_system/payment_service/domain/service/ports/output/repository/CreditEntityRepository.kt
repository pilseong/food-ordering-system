package net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository

import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry

interface CreditEntityRepository {
    fun save(creditEntry: CreditEntry): CreditEntry

    fun findByCustomerId(customerId: CustomerId): CreditEntry?

}