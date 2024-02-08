package net.philipheur.food_ordering_system.order_service.domain.application_service

import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderId
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderStatus
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentOrderStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStep
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.RestaurantApprovalResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.approval.ApprovalOutboxHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.payment.PaymentOutboxHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.OrderRepository
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainService
import net.philipheur.food_ordering_system.order_service.domain.core.entity.Order
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCancelledEvent
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Component
open class OrderApprovalSaga(
    private val orderDomainService: OrderDomainService,
    private val orderSagaHelper: OrderSagaHelper,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val orderRepository: OrderRepository
) : SagaStep<RestaurantApprovalResponse> {

    private val log by LoggerDelegator()

    // 식당 결과가 진행이면 더 이상 saga 가 진행할 것이 없다. 처리 후 종결
    @Transactional
    override fun process(data: RestaurantApprovalResponse) {
        /* 1. 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <시작> */
        var orderApprovalOutboxMessage = approvalOutboxHelper
            .getApprovalOutboxMessageBySagaIdAndSagaStatus(
                sagaId = data.sagaId,
                sagaStatus = arrayOf(SagaStatus.PROCESSING),
            )

        // 메시지가 없으면 이미 처리 완료로 종결
        if (orderApprovalOutboxMessage == null) {
            log.info(
                "An outbox message with " +
                        "saga id: ${data.sagaId} is already processed!"
            )
            return;
        }
        /* 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <종료> */

        // 2. 비즈니스 로직 처리 OrderStatus -> APPROVED
        val order = completeApproval(data)


        /* 3. 비즈니스 로직 결과에 따른 approval outbox 메시지 업데이트 <시작> */
        // saga 상태 수정 -> SUCCEEDED
        val sagaStatus = orderSagaHelper
            .orderStatusToSagaStatus(order.orderStatus)

        orderApprovalOutboxMessage = orderApprovalOutboxMessage.copy(
            processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
            orderStatus = order.orderStatus!!,
            sagaStatus = sagaStatus
        )

        // approval outbox 메시지 저장
        approvalOutboxHelper.saveOutboxMessage(orderApprovalOutboxMessage)
        /* 비즈니스 로직 결과에 따른 approval outbox 메시지 업데이트 <종료> */


        // 4. payment outbox 메시지 업데이트 SUCCEEDED
        updatePaymentOutboxMessage(
            sagaId = data.sagaId,
            orderStatus = order.orderStatus!!,
            sagaStatus = sagaStatus
        )

        log.info("Order with id: ${order.id!!.value} is approved");
    }

    // 식당 결과가 거절의 경우 payment 로 식당에서 거절해서 결재처리가 복구 되어야 한다.
    @Transactional
    override fun rollback(
        data: RestaurantApprovalResponse
    ) {
        /* 1. 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <시작> */
        var orderApprovalOutboxMessage = approvalOutboxHelper
            .getApprovalOutboxMessageBySagaIdAndSagaStatus(
                sagaId = data.sagaId,
                sagaStatus = arrayOf(SagaStatus.PROCESSING),
            )

        // 메시지가 없으면 이미 처리 완료로 종결
        if (orderApprovalOutboxMessage == null) {
            log.info(
                "An outbox message with " +
                        "saga id: ${data.sagaId} is already processed!"
            )
            return;
        }
        /* 해당 sagaId와 saga 상태로 outbox 메시지를 검색 <종료> */

        // 2. 비즈니스 로직 처리 OrderStatus -> CANCELLING
        val orderCancelledEvent = cancelInit(data)


        /* 3. 비즈니스 로직 결과에 따른 approval outbox 메시지 업데이트 <시작> */
        // order 상태 -> PAID -> CANCELLING / saga 상태 수정 -> PROCESSING -> COMPENSATING
        val sagaStatus = orderSagaHelper
            .orderStatusToSagaStatus(orderCancelledEvent.order.orderStatus)

        orderApprovalOutboxMessage = orderApprovalOutboxMessage.copy(
            processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
            orderStatus = orderCancelledEvent.order.orderStatus!!,
            sagaStatus = sagaStatus
        )

        // approval outbox 메시지 저장
        // saga 상태 -> COMPENSATING, order 상태 -> CANCELLING
        approvalOutboxHelper.saveOutboxMessage(orderApprovalOutboxMessage)
        /* 비즈니스 로직 결과에 따른 approval outbox 메시지 업데이트 <종료> */


        // 4. payment outbox 메시지 생성 저장
        saveNewPaymentOutboxMessage(
            event = orderCancelledEvent,
            restaurantApprovalResponse = data,
            sagaStatus = sagaStatus
        )

        log.info("Order with id: ${orderCancelledEvent.order.id!!.value} is cancelling");
    }


    /*
    * private functions ----------------------------------------------------------
    * */

    private fun saveNewPaymentOutboxMessage(
        event: OrderCancelledEvent,
        restaurantApprovalResponse: RestaurantApprovalResponse,
        sagaStatus: SagaStatus
    ) {
        // order payment outbox 메시지 생성
        // outbox DB에 payload 로 저장할 메시지 객체 생성
        val payload = OrderPaymentEventPayload(
            orderId = event.order.id!!.value.toString(),
            customerId = event.order.customerId.value.toString(),
            price = event.order.price.amount,
            createdAt = event.createdAt,
            paymentOrderStatus = PaymentOrderStatus.CANCELLED.name
        )

        // payment outbox 에 저장
        // order 상태 -> CANCELLING / saga 상태 -> COMPENSATING
        paymentOutboxHelper.saveOutboxMessage(
            OrderPaymentOutboxMessage(
                id = UUID.randomUUID(),
                sagaId = restaurantApprovalResponse.sagaId,
                createdAt = event.createdAt,
                type = SagaConstants.ORDER_SAGA_NAME,
                payload = paymentOutboxHelper.createPayload(payload),
                orderStatus = event.order.orderStatus!!,
                sagaStatus = sagaStatus,
                outboxStatus = OutboxStatus.STARTED
            )
        )
    }

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

    // 식당 수락으로 상태 변경 비즈니스로직 실행
    private fun completeApproval(
        data: RestaurantApprovalResponse
    ): Order {
        log.info("Completing approval for order with id: ${data.orderId}");
        // 주문 검색
        val order = findOrder(data.orderId)
        // 주문 비즈니스 로직 처리 -> OrderStatus 를 PAID 로 변경
        orderDomainService.approveOrder(order)
        // 주문 저장
        orderRepository.save(order)

        return order
    }

    private fun cancelInit(
        data: RestaurantApprovalResponse
    ): OrderCancelledEvent {
        log.info("Cancelling for order payment with id: ${data.orderId}");
        // 주문 검색
        val order = findOrder(data.orderId)
        // 주문 비즈니스 로직 처리 -> OrderStatus 를 PAID 로 변경
        val orderCancelledEvent = orderDomainService
            .cancelOrderPayment(
                order,
                data.failureMessages
            )
        // 주문 저장
        orderRepository.save(order)

        return orderCancelledEvent
    }

    private fun updatePaymentOutboxMessage(
        sagaId: UUID,
        orderStatus: OrderStatus,
        sagaStatus: SagaStatus
    ) {
        var paymentOutboxMessage = paymentOutboxHelper
            .getPaymentOutboxMessageBySagaIdAndSagaStatus(
                sagaId = sagaId,
                sagaStatus = arrayOf(SagaStatus.PROCESSING)
            )

        if (paymentOutboxMessage == null) {
            throw OrderDomainException(
                "Payment outbox message cannot be found " +
                        "in ${SagaStatus.PROCESSING.name} state"
            )
        }

        paymentOutboxMessage = paymentOutboxMessage.copy(
            processedAt = ZonedDateTime.now(ZoneId.of(UTC)),
            orderStatus = orderStatus,
            sagaStatus = sagaStatus
        )


        // approval outbox message 저장
        paymentOutboxHelper.saveOutboxMessage(paymentOutboxMessage)
    }
}