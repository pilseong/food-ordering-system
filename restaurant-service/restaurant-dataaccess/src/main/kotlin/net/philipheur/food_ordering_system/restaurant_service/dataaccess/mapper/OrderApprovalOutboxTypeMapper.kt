package net.philipheur.food_ordering_system.restaurant_service.dataaccess.mapper

import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.restaurant_service.dataaccess.entity.OrderApprovalOutboxEntity
import org.springframework.stereotype.Component

@Component
class OrderApprovalOutboxTypeMapper {
    fun orderApprovalOutboxMessageToEntity(
        message: OrderApprovalOutboxMessage
    ) = OrderApprovalOutboxEntity(
        id = message.id,
        sagaId = message.sagaId,
        processedAt = message.processedAt,
        createdAt = message.createdAt,
        type = message.type,
        payload = message.payload,
        approvalStatus = message.orderApprovalStatus,
        outboxStatus = message.outboxStatus,
        version = message.version
    )

    fun orderApprovalOutboxEntityToMessage(
        entity: OrderApprovalOutboxEntity
    ) = OrderApprovalOutboxMessage(
        id = entity.id,
        sagaId = entity.sagaId,
        createdAt = entity.createdAt,
        processedAt = entity.processedAt,
        type = entity.type,
        payload = entity.payload,
        orderApprovalStatus = entity.approvalStatus,
        outboxStatus = entity.outboxStatus,
        version = entity.version
    )
}