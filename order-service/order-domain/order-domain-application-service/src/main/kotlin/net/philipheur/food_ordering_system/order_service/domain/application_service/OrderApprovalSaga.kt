package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStep
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.RestaurantApprovalResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class OrderApprovalSaga(
    private val orderDomainService: OrderDomainService,
    private val orderSagaHelper: OrderSagaHelper,
    private val publisher: OrderCancelledPaymentRequestMessagePublisher
) : SagaStep<RestaurantApprovalResponse> {

    private val log by LoggerDelegator()

    // 식당 결과가 진행이면 더 이상 saga 가 진행할 것이 없다. 처리 후 종결
    @Transactional
    override fun process(
        data: RestaurantApprovalResponse
    ) {

//    override fun process(
//        data: RestaurantApprovalResponse
//    ): EmptyEvent {
//        log.info("Approving order with id: ${data.orderId}")
//        val order = orderSagaHelper.findOrder(data.orderId)
//        orderDomainService.approveOrder(order)
//        orderSagaHelper.saveOrder(order)
//        log.info("order with id: ${data.orderId} is approved")
//        return EmptyEvent.INSTANCE
//    }
    }

    // 식당 결과가 거절의 경우 payment 로 식당에서 거절해서 결재처리가 복구 되어야 한다.
    @Transactional
    override fun rollback(
        data: RestaurantApprovalResponse
    ) {

//    override fun rollback(
//        data: RestaurantApprovalResponse
//    ): OrderCancelledEvent {
//        log.info("Cancelling order with id: ${data.orderId}")
//        val order = orderSagaHelper.findOrder(data.orderId)
//        val orderCancelledEvent = orderDomainService
//            .cancelOrderPayment(
//                order = order,
//                failureMessages = data.failureMessages,
//                orderCancelledEventPublisher = publisher
//            )
//        orderSagaHelper.saveOrder(order)
//        log.info("order with id: ${data.orderId} is cancelling")
//        return orderCancelledEvent
//    }
    }
}