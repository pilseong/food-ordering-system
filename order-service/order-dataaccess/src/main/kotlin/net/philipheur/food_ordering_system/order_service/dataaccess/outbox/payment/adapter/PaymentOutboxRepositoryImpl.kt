package net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.adapter

import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.mapper.PaymentOutboxTypeMapper
import net.philipheur.food_ordering_system.order_service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.repository.PaymentOutboxRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentOutboxRepositoryImpl(
    private val paymentOutboxJpaRepository: PaymentOutboxJpaRepository,
    private val mapper: PaymentOutboxTypeMapper
) : PaymentOutboxRepository {
    override fun save(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage
    ): OrderPaymentOutboxMessage {
        val entity = paymentOutboxJpaRepository.save(
            mapper.orderPaymentOutboxMessageToEntity(
                orderPaymentOutboxMessage
            )
        )
        return mapper.paymentOutboxEntityToMessage(entity)
    }

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ): List<OrderPaymentOutboxMessage> {

        val entities = paymentOutboxJpaRepository
            .findByTypeAndOutboxStatusAndSagaStatusIn(
                type = type,
                outboxStatus = outboxStatus,
                sagaStatus = sagaStatus.toList()
            )

//        if (entities == null) {
//            throw PaymentOutboxNotFoundException(
//                "Payment outbox object could not be found " +
//                        "for saga type $type"
//            )
//        }

        return entities.map {
            mapper.paymentOutboxEntityToMessage(it)
        }
    }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus
    ) = paymentOutboxJpaRepository
        .findByTypeAndSagaIdAndSagaStatusIn(
            type = type,
            sagaId = sagaId,
            sagaStatus = sagaStatus.toList()
        )
        ?.let { mapper.paymentOutboxEntityToMessage(it) }


    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus
    ) = paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
        type = type,
        outboxStatus = outboxStatus,
        sagaStatus = sagaStatus.toList()
    )
}