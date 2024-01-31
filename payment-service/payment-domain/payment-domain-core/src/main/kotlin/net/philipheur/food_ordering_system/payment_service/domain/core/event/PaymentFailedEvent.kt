package net.philipheur.food_ordering_system.payment_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import java.time.ZonedDateTime

class PaymentFailedEvent(
    payment: Payment,
    createdAt: ZonedDateTime,
    failureMessages: MutableList<String>,
    private val paymentFailedEventPublisher: DomainEventPublisher<PaymentFailedEvent>
) : PaymentEvent(payment, createdAt, failureMessages) {
    override fun fire() {
        paymentFailedEventPublisher.publish(this)
    }
}