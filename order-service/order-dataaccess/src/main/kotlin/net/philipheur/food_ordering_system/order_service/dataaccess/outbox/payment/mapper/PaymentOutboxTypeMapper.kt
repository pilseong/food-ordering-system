package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.mapper

import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import org.springframework.stereotype.Component

@Component
class PaymentOutboxTypeMapper {
    fun orderPaymentOutboxMessageToEntity(
        message: OrderPaymentOutboxMessage
    ) = PaymentOutboxEntity(
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

    fun paymentOutboxEntityToMessage(
        entity: PaymentOutboxEntity
    ) = OrderPaymentOutboxMessage(
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