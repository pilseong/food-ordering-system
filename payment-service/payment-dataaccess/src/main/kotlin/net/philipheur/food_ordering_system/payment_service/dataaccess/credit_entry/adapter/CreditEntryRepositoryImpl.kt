package net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.adapter

import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.entity.CreditEntryEntity
import net.philipheur.food_ordering_system.payment_service.dataaccess.credit_entry.repository.CreditEntryJpaRepository
import net.philipheur.food_ordering_system.common.domain.valueobject.CreditEntryId
import net.philipheur.food_ordering_system.common.domain.valueobject.CustomerId
import net.philipheur.food_ordering_system.common.domain.valueobject.Money
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.CreditEntityRepository
import org.springframework.stereotype.Component

@Component
open class CreditEntryRepositoryImpl(
    private val creditEntryJpaRepository: CreditEntryJpaRepository
) : CreditEntityRepository {
    override fun save(creditEntry: CreditEntry): CreditEntry {
        val creditEntryEntity = creditEntryJpaRepository
            .save(
                CreditEntryEntity(
                    id = creditEntry.id!!.value,
                    customerId = creditEntry.customerId.value,
                    totalCreditAmount = creditEntry.totalCreditAmount.amount
                )
            )
        return CreditEntry(
            creditEntryId = CreditEntryId(creditEntryEntity.id),
            customerId = CustomerId(creditEntryEntity.customerId),
            totalCreditAmount = Money(creditEntryEntity.totalCreditAmount)
        )
    }

    override fun findByCustomerId(customerId: CustomerId): CreditEntry? {
        return creditEntryJpaRepository.findByCustomerId(customerId.value)
            ?.let { entity ->
                CreditEntry(
                    creditEntryId = CreditEntryId(entity.id),
                    customerId = CustomerId(entity.customerId),
                    totalCreditAmount = Money(entity.totalCreditAmount)
                )
            }
    }
}