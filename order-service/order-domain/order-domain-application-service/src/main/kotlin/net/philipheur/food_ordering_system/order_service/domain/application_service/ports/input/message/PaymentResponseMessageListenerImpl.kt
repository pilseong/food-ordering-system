package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import org.springframework.stereotype.Service

@Service
class PaymentResponseMessageListenerImpl: PaymentResponseMessageListener {

    private val log by LoggerDelegator()

    override fun paymentCompleted(paymentResponse: PaymentResponse) {
        log.info("payment completed with id ${paymentResponse.id}")
    }

    override fun paymentCancelled(paymentResponse: PaymentResponse) {
        log.error("payment failed with ${paymentResponse.failureMessages}")
    }
}