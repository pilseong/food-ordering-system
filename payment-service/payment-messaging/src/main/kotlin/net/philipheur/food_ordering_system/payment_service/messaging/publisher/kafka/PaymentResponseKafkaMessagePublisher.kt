package net.philipheur.food_ordering_system.payment_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.config.PaymentServiceConfigData
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderOutboxMessage
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.PaymentEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentResponseMessagePublisher
import net.philipheur.food_ordering_system.payment_service.messaging.mapper.KafkaMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentResponseKafkaMessagePublisher(
    private val paymentServiceConfigData: PaymentServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, PaymentResponseAvroModel>,
    private val kafkaMessageHelper: KafkaMessageHelper,
): PaymentResponseMessagePublisher {

    private val log by LoggerDelegator()
    override fun publish(
        orderOutboxMessage: OrderOutboxMessage,
        outboxCallback: (OrderOutboxMessage, OutboxStatus) -> Unit
    ) {
        val payload = kafkaMessageHelper.getPaymentEventPayload(
            payload = orderOutboxMessage.payload,
            outputType = PaymentEventPayload::class.java
        )

        val sagaId = orderOutboxMessage.sagaId

        log.info(
            "Received ${orderOutboxMessage.paymentStatus} OrderOutboxMessage for " +
                    "order id: {}", payload.orderId
        )

        try {
            val paymentResponseAvroModel = PaymentResponseAvroModel(
                UUID.randomUUID(),
                sagaId,
                UUID.fromString(payload.paymentId),
                UUID.fromString(payload.customerId),
                UUID.fromString(payload.orderId),
                payload.price,
                payload.createdAt.toInstant(),
                PaymentStatus.valueOf(payload.paymentStatus),
                payload.failureMessages
            )

            // 키는 sagaId로 처리해야 같은 partition 에서 처리 가능하다.
            kafkaProducer.send(
                topicName = paymentServiceConfigData.paymentResponseTopicName,
                key = orderOutboxMessage.sagaId.toString(),
                message = paymentResponseAvroModel,
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
                    log.info(
                        "Received ${orderOutboxMessage.paymentStatus} successful response " +
                                "from Kafka for " +
                                "order id: $payload.orderId " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )

                    outboxCallback(
                        orderOutboxMessage,
                        OutboxStatus.COMPLETED
                    )
                } else {
                    log.error(
                        "Error while sending PaymentResponseAvroModel" +
                                "message $paymentResponseAvroModel " +
                                "to topic ${paymentServiceConfigData.paymentResponseTopicName}",
                        ex
                    );

                    outboxCallback(
                        orderOutboxMessage,
                        OutboxStatus.FAILED
                    )
                }
            }

            log.info(
                "PaymentResponseAvroModel sent to Kafka " +
                        "for order id: ${payload.orderId} and " +
                        "saga id: ${orderOutboxMessage.sagaId}"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending PaymentResponseAvroModel message " +
                        "to kafka with " +
                        "order id: ${payload.orderId}, " +
                        "saga id: ${orderOutboxMessage.sagaId}" +
                        "error: ${ex.message}",
                ex
            )
        }
    }
}