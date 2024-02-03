package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.approval

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.ApprovalOutboxRepository
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Component
open class ApprovalOutboxHelper(
    private val approvalOutboxRepository: ApprovalOutboxRepository,
    private val objectMapper: ObjectMapper
) {
    private val log by LoggerDelegator()

    @Transactional(readOnly = true)
    open fun getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
        type = SagaConstants.ORDER_SAGA_NAME,
        outboxStatus = outboxStatus,
        sagaStatus = sagaStatus
    )

    @Transactional(readOnly = true)
    open fun getApprovalOutboxMessageBySagaIdAndSagaStatus(
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ) = approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(
        type = SagaConstants.ORDER_SAGA_NAME,
        sagaId = sagaId,
        sagaStatus = sagaStatus
    )


    @Transactional
    fun saveOutboxMessage(orderApprovalOutboxMessage: OrderApprovalOutboxMessage) {
        try {
            approvalOutboxRepository.save(orderApprovalOutboxMessage)
            log.info(
                "OrderApprovalOutboxMessage saved with " +
                        "outbox id: ${orderApprovalOutboxMessage.id}"
            )
        } catch (ex: Exception) {
            log.error(
                "Could not save OrderApprovalOutboxMessage with " +
                        "outbox id: ${orderApprovalOutboxMessage.id}"
            )

            throw OrderDomainException(
                "Could not save OrderApprovalOutboxMessage with " +
                        "outbox id: ${orderApprovalOutboxMessage.id}"
            )
        }
    }

    @Transactional
    open fun deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
            type = SagaConstants.ORDER_SAGA_NAME,
            outboxStatus = outboxStatus,
            sagaStatus = sagaStatus
        )
    }

    fun createPayload(orderApprovalEventPayload: OrderApprovalEventPayload): String {
        try {
            return objectMapper.writeValueAsString(orderApprovalEventPayload)
        } catch (e: JsonProcessingException) {
            log.error(
                "Could not create OrderApprovalEventPayload for " +
                        "order id: ${orderApprovalEventPayload.orderId}",
                e
            )
            throw OrderDomainException(
                "Could not create OrderApprovalEventPayload for " +
                        "order id: ${orderApprovalEventPayload.orderId}",
                e
            )
        }
    }
}