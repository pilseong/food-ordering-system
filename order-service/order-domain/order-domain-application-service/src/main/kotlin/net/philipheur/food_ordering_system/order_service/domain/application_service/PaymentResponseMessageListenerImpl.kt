package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.PaymentResponseMessageListener
import org.springframework.stereotype.Service

// 카프카에서 수신한 결제 결과를 받아 처리 한다.
@Service
class PaymentResponseMessageListenerImpl(
    private val orderPaymentSaga: OrderPaymentSaga
) : PaymentResponseMessageListener {

    private val log by LoggerDelegator()

    // PaymentStatus -> COMPLETED
    override fun paymentCompleted(response: PaymentResponse) {
        orderPaymentSaga.process(response)
        log.info(
            "Publishing OrderPaidEvent " +
                    "for order id ${response.orderId}"
        )
    }

    // PaymentStatus -> CANCELLED, FAILED
    override fun paymentCancelled(response: PaymentResponse) {
        orderPaymentSaga.rollback(response)
        log.error(
            "Order id ${response.orderId} is roll backed " +
                    "with failure messages ${response.failureMessages}"
        )
    }
}