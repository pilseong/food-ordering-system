package net.philipheur.food_ordering_system.payment_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditEntryId
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money

class CreditEntry(
    creditEntryId: CreditEntryId,
    val customerId: CustomerId,
    var totalCreditAmount: Money
) : BaseEntity<CreditEntryId>(creditEntryId) {

    fun addCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.add(amount)
    }

    fun subtractCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.subtract(amount)
    }
}