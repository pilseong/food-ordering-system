package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.payment

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants.Companion.ORDER_SAGA_NAME
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.PaymentOutboxRepository
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Component
open class PaymentOutboxHelper(
    private val paymentOutboxRepository: PaymentOutboxRepository,
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()

    @Transactional(readOnly = true)
    open fun getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
        type = ORDER_SAGA_NAME,
        outboxStatus = outboxStatus,
        sagaStatus = sagaStatus
    )

    @Transactional(readOnly = true)
    open fun getPaymentOutboxMessageBySagaIdAndSagaStatus(
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ) = paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
        type = ORDER_SAGA_NAME,
        sagaId = sagaId,
        sagaStatus = sagaStatus
    )

    @Transactional
    open fun saveOutboxMessage(orderPaymentOutboxMessage: OrderPaymentOutboxMessage) {

        try {
            paymentOutboxRepository.save(orderPaymentOutboxMessage)
            log.info(
                "OrderPaymentOutboxMessage saved with " +
                        "outbox id: ${orderPaymentOutboxMessage.id}"
            )
        } catch (ex: Exception) {
            log.error(
                "Could not save OrderPaymentOutboxMessage with " +
                        "outbox id: ${orderPaymentOutboxMessage.id}"
            )
            throw OrderDomainException(
                "Could not save OrderPaymentOutboxMessage with " +
                        "outbox id: ${orderPaymentOutboxMessage.id}"
            )
        }
    }

    @Transactional
    open fun deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) {
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
            sagaStatus = sagaStatus
        )
    }

    fun createPayload(orderPaymentEventPayload: OrderPaymentEventPayload): String {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload)
        } catch (e: JsonProcessingException) {
            log.error(
                "Could not create OrderPaymentEventPayload for " +
                        "order id: ${orderPaymentEventPayload.orderId}",
                e
            )
            throw OrderDomainException(
                "Could not create OrderPaymentEventPayload for " +
                        "order id: ${orderPaymentEventPayload.orderId}",
                e
            )
        }
    }
}