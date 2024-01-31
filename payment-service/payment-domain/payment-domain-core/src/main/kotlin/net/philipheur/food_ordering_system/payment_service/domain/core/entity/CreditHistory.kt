package net.philipheur.food_ordering_system.payment_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.BaseEntity
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditHistoryId
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.payment_service.domain.core.valueobject.TransactionType

class CreditHistory(
    creditHistoryId: CreditHistoryId?,
    val customerId: CustomerId,
    var amount: Money,
    var transactionType: TransactionType? = null
): BaseEntity<CreditHistoryId>(creditHistoryId) {
}