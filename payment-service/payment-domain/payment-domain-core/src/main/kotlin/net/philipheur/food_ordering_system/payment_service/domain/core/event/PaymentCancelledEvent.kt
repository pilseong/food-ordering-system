package net.philipheur.food_ordering_system.payment_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import java.time.ZonedDateTime

class PaymentCancelledEvent(
    payment: Payment,
    createdAt: ZonedDateTime,
    private val paymentCancelledPublisher: DomainEventPublisher<PaymentCancelledEvent>
) : PaymentEvent(payment, createdAt) {
    override fun fire() {
        paymentCancelledPublisher.publish(this)
    }
}