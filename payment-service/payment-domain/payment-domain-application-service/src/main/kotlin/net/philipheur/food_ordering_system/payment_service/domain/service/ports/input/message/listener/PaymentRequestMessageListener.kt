package net.philipheur.food_ordering_system.payment_service.domain.service.ports.input.message.listener

import net.philipheur.food_ordering_system.payment_service.domain.service.dto.request.PaymentRequest

interface PaymentRequestMessageListener {

    fun completePayment(paymentRequest: PaymentRequest)
    fun cancelPayment(paymentRequest: PaymentRequest)
}