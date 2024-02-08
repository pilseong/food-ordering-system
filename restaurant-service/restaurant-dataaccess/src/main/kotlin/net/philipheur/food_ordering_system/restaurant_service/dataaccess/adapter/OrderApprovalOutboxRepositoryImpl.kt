package net.philipheur.food_ordering_system.restaurant_service.dataaccess.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.mapper.OrderApprovalOutboxTypeMapper
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.repository.OrderApprovalOutboxJpaRepository
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.OrderApprovalOutboxRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderApprovalOutboxRepositoryImpl(
    private val repository: OrderApprovalOutboxJpaRepository,
    private val mapper: OrderApprovalOutboxTypeMapper,
) : OrderApprovalOutboxRepository {
    override fun save(
        orderOutboxMessage: OrderApprovalOutboxMessage
    ): OrderApprovalOutboxMessage {

        val entity = repository.save(
            mapper.orderApprovalOutboxMessageToEntity(orderOutboxMessage)
        )

        return mapper.orderApprovalOutboxEntityToMessage(entity)
    }

    override fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus
    ): List<OrderApprovalOutboxMessage> {
        val entities = repository
            .findByTypeAndOutboxStatus(
                type = type,
                outboxStatus = outboxStatus

            )
        return entities.map {
            mapper.orderApprovalOutboxEntityToMessage(it)
        }
    }

    override fun findByTypeAndSagaIdAndOrderApprovalStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        approvalStatus: OrderApprovalStatus,
        outboxStatus: OutboxStatus
    ) = repository.findByTypeAndSagaIdAndApprovalStatusAndOutboxStatus(
        type = type,
        sagaId = sagaId,
        approvalStatus = approvalStatus,
        outboxStatus = outboxStatus
    )?.let { mapper.orderApprovalOutboxEntityToMessage(it) }

    override fun deleteByTypeAndOutboxStatus(
        type: String, outboxStatus: OutboxStatus
    ) = repository.deleteByTypeAndOutboxStatus(
        type = type,
        outboxStatus = outboxStatus
    )
}