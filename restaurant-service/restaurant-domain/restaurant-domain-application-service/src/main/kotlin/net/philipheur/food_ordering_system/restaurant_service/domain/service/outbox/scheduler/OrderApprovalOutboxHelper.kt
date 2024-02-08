package net.philipheur.food_ordering_system.restaurant_service.domain.service.outbox.scheduler

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants.Companion.ORDER_SAGA_NAME
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.restaurant_service.domain.core.exception.RestaurantDomainException
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.OrderApprovalOutboxRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
open class OrderApprovalOutboxHelper(
    private val repository: OrderApprovalOutboxRepository,
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()

    // Saga 를 관리하는 것이 아니기 때문에 처리되지 않는 메시지만 확인한다.
    @Transactional(readOnly = true)
    open fun getOrderOutboxMessageByOutboxStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = repository.findByTypeAndOutboxStatus(
        type = ORDER_SAGA_NAME,
        outboxStatus = outboxStatus,
    )

    @Transactional(readOnly = true)
    open fun getPaymentOutboxMessageBySagaIdAndOrderApprovalStatusAndOutboxStatus(
        sagaId: UUID,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus,
    ) = repository.findByTypeAndSagaIdAndOrderApprovalStatusAndOutboxStatus(
        type = ORDER_SAGA_NAME,
        sagaId = sagaId,
        approvalStatus = approvalStatus,
        outboxStatus = outboxStatus,
    )

    @Transactional
    open fun saveOutboxMessage(
        outboxMessage: OrderApprovalOutboxMessage
    ) {

        try {
            repository.save(outboxMessage)
            log.info(
                "OrderPaymentOutboxMessage saved with " +
                        "outbox id: ${outboxMessage.id}"
            )
        } catch (ex: Exception) {
            log.error(
                "Could not save OrderOutboxMessage with " +
                        "outbox id: ${outboxMessage.id}"
            )
            throw RestaurantDomainException(
                "Could not save OrderOutboxMessage with " +
                        "outbox id: ${outboxMessage.id}"
            )
        }
    }

    @Transactional
    open fun deleteOrderOutboxMessageByOutboxStatus(
        outboxStatus: OutboxStatus,
    ) {
        repository.deleteByTypeAndOutboxStatus(
            type = ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
        )
    }

    open fun createPayload(payload: OrderApprovalEventPayload): String {
        try {
            return objectMapper.writeValueAsString(payload)
        } catch (e: JsonProcessingException) {
            log.error(
                "Could not create OrderApprovalEventPayload for " +
                        "order id: ${payload.orderId}",
                e
            )
            throw RestaurantDomainException(
                "Could not create OrderApprovalEventPayload for " +
                        "order id: ${payload.orderId}",
                e
            )
        }
    }
}