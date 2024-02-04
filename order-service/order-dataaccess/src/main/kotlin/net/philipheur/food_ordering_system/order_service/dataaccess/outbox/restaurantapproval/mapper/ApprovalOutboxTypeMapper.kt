package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.mapper

import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import org.springframework.stereotype.Component

@Component
class ApprovalOutboxTypeMapper {
    fun approvalOutboxMessageToEntity(
        message: OrderApprovalOutboxMessage
    ) = ApprovalOutboxEntity(
        id = message.id,
        sagaId = message.sagaId,
        processedAt = message.processedAt,
        createdAt = message.createdAt,
        type = message.type,
        payload = message.payload,
        orderStatus = message.orderStatus,
        sagaStatus = message.sagaStatus,
        outboxStatus = message.outboxStatus,
        version = message.version
    )

    fun approvalOutboxEntityToMessage(
        entity: ApprovalOutboxEntity
    ) = OrderApprovalOutboxMessage(
        id = entity.id,
        sagaId = entity.sagaId,
        createdAt = entity.createdAt,
        processedAt = entity.processedAt,
        type = entity.type,
        payload = entity.payload,
        orderStatus = entity.orderStatus,
        sagaStatus = entity.sagaStatus,
        outboxStatus = entity.outboxStatus,
        version = entity.version
    )
}