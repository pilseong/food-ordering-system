package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.scheduler

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants.Companion.ORDER_SAGA_NAME
import net.philipheur.food_ordering_system.payment_service.domain.core.exception.PaymentDomainException
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.PaymentEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.OrderOutboxRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
open class OrderOutboxHelper(
    private val orderOutboxRepository: OrderOutboxRepository,
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()

    // Saga 를 관리하는 것이 아니기 때문에 처리되지 않는 메시지만 확인한다.
    @Transactional(readOnly = true)
    open fun getOrderOutboxMessageByOutboxStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = orderOutboxRepository.findByTypeAndOutboxStatus(
        type = ORDER_SAGA_NAME,
        outboxStatus = outboxStatus,
    )

    @Transactional(readOnly = true)
    open fun getPaymentOutboxMessageBySagaIdAndPaymentStatusAndOutboxStatus(
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus,
    ) = orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type = ORDER_SAGA_NAME,
        sagaId = sagaId,
        paymentStatus = paymentStatus,
        outboxStatus = outboxStatus,
    )

    @Transactional
    open fun saveOutboxMessage(orderOutboxMessage: OrderOutboxMessage) {

        try {
            orderOutboxRepository.save(orderOutboxMessage)
            log.info(
                "OrderPaymentOutboxMessage saved with " +
                        "outbox id: ${orderOutboxMessage.id}"
            )
        } catch (ex: Exception) {
            log.error(
                "Could not save OrderOutboxMessage with " +
                        "outbox id: ${orderOutboxMessage.id}"
            )
            throw PaymentDomainException(
                "Could not save OrderOutboxMessage with " +
                        "outbox id: ${orderOutboxMessage.id}"
            )
        }
    }

    @Transactional
    open fun deleteOrderOutboxMessageByOutboxStatus(
        outboxStatus: OutboxStatus,
    ) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
        )
    }

    open fun createPayload(paymentEventPayload: PaymentEventPayload): String {
        try {
            return objectMapper.writeValueAsString(paymentEventPayload)
        } catch (e: JsonProcessingException) {
            log.error(
                "Could not create OrderEventPayload for " +
                        "order id: ${paymentEventPayload.orderId}",
                e
            )
            throw PaymentDomainException(
                "Could not create OrderEventPayload for " +
                        "order id: ${paymentEventPayload.orderId}",
                e
            )
        }
    }
}