package net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.mapper

import net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.entity.OrderOutboxEntity
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import org.springframework.stereotype.Component

@Component
class OrderOutboxTypeMapper {
    fun orderOutboxMessageToEntity(
        message: OrderOutboxMessage
    ) = OrderOutboxEntity(
        id = message.id,
        sagaId = message.sagaId,
        processedAt = message.processedAt,
        createdAt = message.createdAt,
        type = message.type,
        payload = message.payload,
        paymentStatus = message.paymentStatus,
        outboxStatus = message.outboxStatus,
        version = message.version
    )

    fun orderOutboxEntityToMessage(
        entity: OrderOutboxEntity
    ) = OrderOutboxMessage(
        id = entity.id,
        sagaId = entity.sagaId,
        createdAt = entity.createdAt,
        processedAt = entity.processedAt,
        type = entity.type,
        payload = entity.payload,
        paymentStatus = entity.paymentStatus,
        outboxStatus = entity.outboxStatus,
        version = entity.version
    )
}