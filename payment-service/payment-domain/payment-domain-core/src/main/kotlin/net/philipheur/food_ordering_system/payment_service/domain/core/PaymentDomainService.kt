package net.philipheur.food_ordering_system.payment_service.domain.core

import net.philipheur.food_ordering_system.common.domain.event.publisher.DomainEventPublisher
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditEntry
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.CreditHistory
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCancelledEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCompletedEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentFailedEvent

interface PaymentDomainService {
    fun validateAndInitiatePayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>,
        paymentCompletedMessagePublisher: DomainEventPublisher<PaymentCompletedEvent>,
        paymentFailedMessagePublisher: DomainEventPublisher<PaymentFailedEvent>

    ): PaymentEvent

    fun validateAndCancelPayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>,
        paymentCancelledMessagePublisher: DomainEventPublisher<PaymentCancelledEvent>,
        paymentFailedMessagePublisher: DomainEventPublisher<PaymentFailedEvent>
    ): PaymentEvent
}