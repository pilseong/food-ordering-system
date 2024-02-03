package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.PaymentResponseMessageListener
import org.springframework.stereotype.Service

@Service
class PaymentResponseMessageListenerImpl(
    private val orderPaymentSaga: OrderPaymentSaga
) : PaymentResponseMessageListener {

    private val log by LoggerDelegator()

    override fun paymentCompleted(response: PaymentResponse) {
        orderPaymentSaga.process(response)
        log.info(
            "Publishing OrderPaidEvent " +
                    "for order id ${response.orderId}"
        )
        // 식당으로 해당 주문을 요청한다.
//        orderPaidEvent.fire()
    }

    override fun paymentCancelled(response: PaymentResponse) {
        orderPaymentSaga.rollback(response)
        log.error(
            "Order id ${response.orderId} is roll backed " +
                    "with failure messages ${response.failureMessages}"
        )

        // 주문이 종결되어 더이상 처리할 것이 없다.
    }
}