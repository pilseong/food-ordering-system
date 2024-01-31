package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.adapter

import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.entity.CreditHistoryEntity
import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_history.repository.CreditHistoryJpaRepository
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditHistoryId
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditHistoryRepository
import org.springframework.stereotype.Component


@Component
open class CreditHistoryRepositoryImpl(
    private val creditHistoryJpaRepository: CreditHistoryJpaRepository
) : CreditHistoryRepository {
    override fun save(creditHistory: CreditHistory): CreditHistory {
        val creditHistoryEntity = creditHistoryJpaRepository
            .save(
                CreditHistoryEntity(
                    id = creditHistory.id!!.value,
                    customerId = creditHistory.customerId.value,
                    amount = creditHistory.amount.amount,
                    type = creditHistory.transactionType!!
                )
            )

        return CreditHistory(
            creditHistoryId = CreditHistoryId(creditHistoryEntity.id),
            customerId = CustomerId(creditHistoryEntity.customerId),
            amount = Money(creditHistoryEntity.amount),
            transactionType = creditHistoryEntity.type,
        )
    }

    override fun findByCustomerId(customerId: CustomerId): MutableList<CreditHistory> {
        val creditHistoryList = creditHistoryJpaRepository
            .findByCustomerId(customerId.value)

        return creditHistoryList.map { entity ->
            CreditHistory(
                creditHistoryId = CreditHistoryId(entity.id),
                customerId = CustomerId(entity.customerId),
                amount = Money(entity.amount),
                transactionType = entity.type,
            )
        }.toMutableList()
    }

}