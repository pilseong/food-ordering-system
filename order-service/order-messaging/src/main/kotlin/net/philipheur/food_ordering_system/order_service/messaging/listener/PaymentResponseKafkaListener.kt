package net.philipheur.food_ordering_system.order_service.messaging.listener

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.consumer.KafkaConsumer
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentStatus.*
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.PaymentResponseMessageListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

// 카프카에서 수신한 메시지를 domain 에서 정의한 Listener 인터페이스로 호출해 준다.
@Component
class PaymentResponseKafkaListener(
    private val paymentResponseMessageListener: PaymentResponseMessageListener,
) : KafkaConsumer<PaymentResponseAvroModel> {

    private val log by LoggerDelegator()

    @KafkaListener(
        id = "\${kafka-consumer-config.payment-consumer-group-id}",
        topics = ["\${order-service.payment-response-topic-name}"]
    )
    override fun receive(
        @Payload models: List<PaymentResponseAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "${models.size} number of payment responses received with " +
                    "keys:$keys, partitions:$partitions and offsets: $offsets",
        )

        models.forEach { model ->
            if (model.paymentStatus == COMPLETED) {
                log.info(
                    "Processing successful payment " +
                            "for order id: ${model.orderId}"
                )
                paymentResponseMessageListener.paymentCompleted(
                    modelToMessage(model)
                )
            } else if (
                model.paymentStatus == CANCELLED ||
                model.paymentStatus == FAILED
            ) {
                log.info("Processing unsuccessful payment for order id: ${model.orderId}", )
                paymentResponseMessageListener.paymentCancelled(
                    modelToMessage(model)
                )
            }
        }
    }

    private fun modelToMessage(msg: PaymentResponseAvroModel) =
        PaymentResponse(
            id = msg.id,
            sagaId = msg.sagaId,
            paymentId = msg.paymentId,
            customerId = msg.customerId,
            orderId = msg.orderId,
            price = msg.price,
            createdAt = msg.createdAt,
            paymentStatus =
            PaymentStatus
                .valueOf(msg.paymentStatus.name),
            failureMessages = msg.failureMessages,
        )
}