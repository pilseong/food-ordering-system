package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.RestaurantApprovalResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.RestaurantApprovalResponseMessageListener
import org.springframework.stereotype.Service

@Service
class RestaurantApprovalResponseMessageListenerImpl(
    private val orderApprovalSaga: OrderApprovalSaga
) :
    RestaurantApprovalResponseMessageListener {

    private val log by LoggerDelegator()

    // 식당에서 주문이 승인되었다는 메시지 수신
    override fun orderApproved(
        response: RestaurantApprovalResponse
    ) {
        orderApprovalSaga.process(response)
        log.info(
            "Order is approved for " +
                    "order id ${response.orderId}"
        )
    }

    // 주문이 거절을 받았을 때 메시지 처리
    override fun orderRejected(
        response: RestaurantApprovalResponse
    ) {
//        val orderCancelledEvent = orderApprovalSaga
//            .rollback(response)
        log.error(
            "Order id ${response.orderId} is roll backed " +
                    "with failure messages ${response.failureMessages}"
        )
//        orderCancelledEvent.fire()
    }
}