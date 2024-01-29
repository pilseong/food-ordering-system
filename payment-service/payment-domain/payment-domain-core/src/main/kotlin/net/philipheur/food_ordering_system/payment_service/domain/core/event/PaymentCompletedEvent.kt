package net.philipheur.food_ordering_system.payment_service.domain.core.event

import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import java.time.ZonedDateTime

class PaymentCompletedEvent(
    payment: Payment,
    createdAt: ZonedDateTime,
    failureMessages: MutableList<String>
) : PaymentEvent(payment, createdAt, failureMessages) {
}