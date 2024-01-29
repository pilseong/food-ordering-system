import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment

interface PaymentDomainService {
    fun validateAndInitiatePayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    )

    fun validateAndCancelPayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    )
}