package net.philipheur.food_ordering_system.payment_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditEntryId
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money

class CreditEntry(
    id: CreditEntryId,
    val customerId: CustomerId,
    var totalCreditAmount: Money
) : BaseEntity<CreditEntryId>(id) {

    fun addCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.add(amount)
    }

    fun subtractCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.subtract(amount)
    }
}