package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.adapter

import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxTypeMapper
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.ApprovalOutboxRepository
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class ApprovalOutboxRepositoryImpl(
    private val approvalOutboxJpaRepository: ApprovalOutboxJpaRepository,
    private val mapper: ApprovalOutboxTypeMapper
) : ApprovalOutboxRepository {
    override fun save(
        approvalOutboxMessage: OrderApprovalOutboxMessage
    ): OrderApprovalOutboxMessage {

        val entity = approvalOutboxJpaRepository.save(
            mapper.approvalOutboxMessageToEntity(
                approvalOutboxMessage
            )
        )
        return mapper.approvalOutboxEntityToMessage(entity)
    }

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderApprovalOutboxMessage> {

        val entities = approvalOutboxJpaRepository
            .findByTypeAndOutboxStatusAndSagaStatusIn(
                type = type,
                outboxStatus = outboxStatus,
                sagaStatus = sagaStatus.toList()
            )

        if (entities == null) {
            throw ApprovalOutboxNotFoundException(
                "Approval outbox object could not be found " +
                        "for saga type $type"
            )
        }

        return entities.map {
            mapper.approvalOutboxEntityToMessage(it)
        }
    }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ) = approvalOutboxJpaRepository
        .findByTypeAndSagaIdAndSagaStatusIn(
            type = type,
            sagaId = sagaId,
            sagaStatus = sagaStatus.toList()
        )
        ?.let { mapper.approvalOutboxEntityToMessage(it) }


    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = approvalOutboxJpaRepository
        .deleteByTypeAndOutboxStatusAndSagaStatusIn(
            type = type,
            outboxStatus = outboxStatus,
            sagaStatus = sagaStatus.toList()
        )
}