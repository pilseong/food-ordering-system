package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantOrderStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStep
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalEventProduct
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.approval.ApprovalOutboxHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.payment.PaymentOutboxHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainService
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderPaidEvent
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderNotFoundException
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

// payment 서비스에서 결제 결과를 수신한 후에 결과에 따라 계속 진행할지
// 다시 roll back 할지를 결정하는 부분을 담당한다.
@Component
open class OrderPaymentSaga(
    private val orderRepository: OrderRepository,
    private val orderDomainService: OrderDomainService,
    private val orderSagaHelper: OrderSagaHelper,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
) : SagaStep<PaymentResponse> {

    private val log by LoggerDelegator()

    // 지불 완료 메시지 처리
    @Transactional
    override fun process(data: PaymentResponse) {
        /* 1. 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <시작> */
        var orderPaymentOutboxMessage = paymentOutboxHelper
            .getPaymentOutboxMessageBySagaIdAndSagaStatus(
                sagaId = data.sagaId,
                sagaStatus = arrayOf(SagaStatus.STARTED),
            )

        // 메시지가 없으면 이미 처리 완료로 종결
        if (orderPaymentOutboxMessage == null) {
            log.info(
                "An outbox message with " +
                        "saga id: ${data.sagaId} is already processed!"
            )
            return;
        }
        /* 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <종료> */

        // 2. 비즈니스 로직 처리
        val orderPaidEvent = completePayOrder(data)


        /* 3. 비즈니스 로직 결과에 따른 payment outbox 메시지 업데이트 <시작> */
        // saga 상태 수정
        val sagaStatus = orderSagaHelper
            .orderStatusToSagaStatus(orderPaidEvent.order.orderStatus)

        orderPaymentOutboxMessage = orderPaymentOutboxMessage.copy(
            processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
            orderStatus = orderPaidEvent.order.orderStatus!!,
            sagaStatus = sagaStatus
        )

        // payment outbox 메시지 저장
        paymentOutboxHelper.saveOutboxMessage(orderPaymentOutboxMessage)
        /* 비즈니스 로직 결과에 따른 payment outbox 메시지 업데이트 <종료> */


        // 4. approval outbox 메시지 생성 및 저장
        saveNewApprovalOutboxMessage(
            orderPaidEvent = orderPaidEvent,
            paymentResponse = data,
            sagaStatus = sagaStatus
        )

        log.info("Order with id: ${orderPaidEvent.order.id!!.value} is paid");
    }

    @Transactional
    override fun rollback(data: PaymentResponse) {

        // 둘 중에 하나인 상태가 있을 수 있다. STARTED OR PROCESSING
        var orderPaymentOutboxMessage = paymentOutboxHelper
            .getPaymentOutboxMessageBySagaIdAndSagaStatus(
                sagaId = data.sagaId,
                sagaStatus = orderSagaHelper
                    .getCurrentSagaStatus(data.paymentStatus)
            )

        if (orderPaymentOutboxMessage == null) {
            log.info(
                "An outbox message with " +
                        "saga id: ${data.sagaId} is already roll backed!"
            );
            return;
        }

        log.info("Cancelling order with id: ${data.orderId}")
        val order = findOrder(data.orderId)
        orderDomainService.cancelOrder(order, data.failureMessages)
        orderRepository.save(order)

        val sagaStatus = orderSagaHelper
            .orderStatusToSagaStatus(order.orderStatus)

        // payment outbox 메시지 업데이트
        orderPaymentOutboxMessage = orderPaymentOutboxMessage.copy(
            processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
            orderStatus = order.orderStatus!!,
            sagaStatus = sagaStatus
        )

        paymentOutboxHelper.saveOutboxMessage(orderPaymentOutboxMessage)

        // 이 경우는 approval outbox message 가 이미 데이터베이스에 존재 할 수 있다.
        // 업데이트만 처리
        if (data.paymentStatus == PaymentStatus.CANCELLED) {
            // 기존 메시지 검색
            var orderApprovalOutboxMessage = approvalOutboxHelper
                .getApprovalOutboxMessageBySagaIdAndSagaStatus(
                    sagaId = data.sagaId,
                    sagaStatus = arrayOf(SagaStatus.COMPENSATING)
                )

            if (orderApprovalOutboxMessage == null) {
                log.info(
                    "Approval outbox message could not be found " +
                            "in ${SagaStatus.COMPENSATING.name} status!"
                );
                throw OrderDomainException(
                    "Approval outbox message could not be found " +
                            "in ${SagaStatus.COMPENSATING.name} status!"
                )
            }

            // payment outbox 메시지 업데이트
            orderApprovalOutboxMessage = orderApprovalOutboxMessage.copy(
                processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
                orderStatus = order.orderStatus!!,
                sagaStatus = sagaStatus
            )

            // approval outbox message 저장
            approvalOutboxHelper.saveOutboxMessage(orderApprovalOutboxMessage)
        }

        log.info("Order with id: ${order.id!!.value} is cancelled")
    }


    /*
    * private functions ----------------------------------------------------------
    * */
    // 주문 찾기
    private fun findOrder(orderId: UUID): Order {
        return orderRepository.findByOrderId(OrderId(orderId))
            ?: run {
                log.error("Order with id: $orderId could not be found")
                throw OrderNotFoundException(
                    "Order with id: $orderId could not be found"
                )
            }
    }

    //
    private fun completePayOrder(data: PaymentResponse): OrderPaidEvent {
        log.info("Completing payment for order with id: ${data.sagaId}");
        // 주문 검색
        val order = findOrder(data.orderId)
        // 주문 비즈니스 로직 처리 -> OrderStatus 를 PAID 로 변경
        val orderPaidEvent = orderDomainService.payOrder(order)
        // 주문 저장
        orderRepository.save(order)

        return orderPaidEvent
    }

    private fun saveNewApprovalOutboxMessage(
        orderPaidEvent: OrderPaidEvent,
        paymentResponse: PaymentResponse,
        sagaStatus: SagaStatus
    ) {
        // order approval outbox 메시지 생성
        val payload = OrderApprovalEventPayload(
            orderId = orderPaidEvent.order.id.toString(),
            restaurantId = orderPaidEvent.order.restaurantId.value.toString(),
            restaurantOrderStatus = RestaurantOrderStatus.PAID.name,
            products = orderPaidEvent.order.items.map {
                OrderApprovalEventProduct(
                    id = it.product.id!!.value.toString(),
                    quantity = it.quantity,
                )
            },
            price = orderPaidEvent.order.price.amount,
            createdAt = orderPaidEvent.createdAt
        )

        // approval outbox message 저장
        approvalOutboxHelper.saveOutboxMessage(
            OrderApprovalOutboxMessage(
                id = UUID.randomUUID(),
                sagaId = paymentResponse.sagaId,
                createdAt = payload.createdAt,
                type = SagaConstants.ORDER_SAGA_NAME,
                payload = approvalOutboxHelper.createPayload(payload),
                orderStatus = orderPaidEvent.order.orderStatus!!,
                sagaStatus = sagaStatus,
                outboxStatus = OutboxStatus.STARTED
            )
        )
    }
}