package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message

import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse

interface PaymentResponseMessageListener {
    fun paymentCompleted(response: PaymentResponse)
    fun paymentCancelled(response: PaymentResponse)
}