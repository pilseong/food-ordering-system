package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message

import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import org.springframework.stereotype.Service

@Service
class PaymentResponseMessageListenerImpl: PaymentResponseMessageListener {
    override fun paymentCompleted(paymentResponse: PaymentResponse) {
        println("payment completed with id ${paymentResponse.id}")
    }

    override fun paymentCancelled(paymentResponse: PaymentResponse) {
        println("payment failed with ${paymentResponse.failureMessages}")
    }
}