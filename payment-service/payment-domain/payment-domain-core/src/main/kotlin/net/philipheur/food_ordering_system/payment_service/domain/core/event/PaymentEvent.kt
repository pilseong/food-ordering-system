package net.philipheur.food_ordering_system.payment_service.domain.core.event

import net.philipheur.food_ordering_system.common.domain.event.DomainEvent
import net.philipheur.food_ordering_system.payment_service.domain.core.entity.Payment
import java.time.ZonedDateTime

open class PaymentEvent(
    val payment: Payment,
    val createAt: ZonedDateTime,
    val failureMessages: MutableList<String> = mutableListOf()
): DomainEvent<Payment>