package net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.adapter

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.mapper.OrderOutboxTypeMapper
import net.philipheur.food_ordering_system.payment_service.dataaccess.outbox.order.repository.OrderOutboxJpaRepository
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.repository.OrderOutboxRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderOutboxRepositoryImpl(
    private val orderOutboxJpaRepository: OrderOutboxJpaRepository,
    private val mapper: OrderOutboxTypeMapper,
) : OrderOutboxRepository {
    override fun save(
        orderOutboxMessage: OrderOutboxMessage
    ): OrderOutboxMessage {
        val entity = orderOutboxJpaRepository.save(
            mapper.orderOutboxMessageToEntity(orderOutboxMessage)
        )
        return mapper.orderOutboxEntityToMessage(entity)
    }

    override fun findByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus
    ): List<OrderOutboxMessage> {
        val entities = orderOutboxJpaRepository
            .findByTypeAndOutboxStatus(
                type = type,
                outboxStatus = outboxStatus
            )

        return entities.map {
            mapper.orderOutboxEntityToMessage(it)
        }
    }

    override fun findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
        type: String,
        sagaId: UUID,
        paymentStatus: PaymentStatus,
        outboxStatus: OutboxStatus
    ) = orderOutboxJpaRepository
        .findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            type = type,
            sagaId = sagaId,
            paymentStatus = paymentStatus,
            outboxStatus = outboxStatus,
        )
        ?.let { mapper.orderOutboxEntityToMessage(it) }

    override fun deleteByTypeAndOutboxStatus(
        type: String,
        outboxStatus: OutboxStatus
    ) = orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(
        type = type,
        outboxStatus = outboxStatus
    )
}