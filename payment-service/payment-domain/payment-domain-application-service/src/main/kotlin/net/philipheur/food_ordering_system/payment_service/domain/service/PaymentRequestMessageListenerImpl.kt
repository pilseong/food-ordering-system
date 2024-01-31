package net.philipheur.food_ordering_system.payment_service.domain.service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.payment_service.domain.service.dto.request.PaymentRequest
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.input.message.listener.PaymentRequestMessageListener
import org.springframework.stereotype.Service

@Service
class PaymentRequestMessageListenerImpl(
    private val paymentRequestHelper: PaymentRequestHelper,
) : PaymentRequestMessageListener {

    private val log by LoggerDelegator()

    // 카프카에서 지불 이벤트를 받아서 처리
    override fun completePayment(paymentRequest: PaymentRequest) {

        // 지불 처리
        val paymentEvent = paymentRequestHelper
            .persisPayment(paymentRequest)


        log.info("Publishing payment event with " +
                "payment id: ${paymentEvent.payment.id!!.value} and " +
                "order id: ${paymentEvent.payment.orderId.value}")

        // 수시한 결과를 카프카로 발송한다.
        paymentEvent.fire()
    }

    // 취소 이벤트 처리
    override fun cancelPayment(paymentRequest: PaymentRequest) {
        val paymentEvent = paymentRequestHelper
            .persistCancelPayment(paymentRequest)

        log.info("Publishing payment event with " +
                "payment id: ${paymentEvent.payment.id!!.value} and " +
                "order id: ${paymentEvent.payment.orderId.value}")

        paymentEvent.fire()
    }
}