package net.philipheur.food_ordering_system.payment_service.domain.core.entity

import net.philipheur.food_ordering_system.common.domain.entity.AggregateRoot
import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class Payment(
    paymentId: PaymentId? = null,
    var orderId: OrderId,
    var customerId: CustomerId,
    var price: Money,
    var paymentStatus: PaymentStatus? = null,
    var createdAt: ZonedDateTime? =
        ZonedDateTime.now(ZoneId.of(UTC))
) : AggregateRoot<PaymentId>(paymentId) {

    fun initializePayment() {
        id = PaymentId(UUID.randomUUID())
        createdAt = ZonedDateTime.now(ZoneId.of(UTC))
    }

    fun validatePayment(failureMessages: MutableList<String>) {
        if (!price.isGreaterThanZero()) {
            failureMessages.add("Total price must be greater than zero")
        }
    }

    fun updateState(paymentStatus: PaymentStatus) {
        this.paymentStatus = paymentStatus
    }
}