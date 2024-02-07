package net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentOrderStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentRequestAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.order_service.domain.application_service.config.OrderServiceConfigData
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.payment.OrderPaymentOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.messaging.mapper.KafkaMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderPaymentEventKafkaPublisher(
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>,
    private val kafkaMessageHelper: KafkaMessageHelper,
) : PaymentRequestMessagePublisher {

    private val log by LoggerDelegator()
    override fun publish(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
        outboxCallback: (OrderPaymentOutboxMessage, OutboxStatus) -> Unit
    ) {
        val payload = kafkaMessageHelper.getOrderEventPayload(
            payload = orderPaymentOutboxMessage.payload,
            outputType = OrderPaymentEventPayload::class.java
        )

        val sagaId = orderPaymentOutboxMessage.sagaId

        log.info(
            "Received PaymentOutboxMessage for " +
                    "order id: {}", payload.orderId
        )

        try {
            val paymentRequestAvroModel = PaymentRequestAvroModel(
                UUID.randomUUID(),
                sagaId,
                UUID.fromString(payload.customerId),
                UUID.fromString(payload.orderId),
                payload.price,
                payload.createdAt.toInstant(),
                PaymentOrderStatus.valueOf(payload.paymentOrderStatus)
            )

            // 키는 sagaId로 처리해야 같은 partition 에서 처리 가능하다.
            kafkaProducer.send(
                topicName = orderServiceConfigData.paymentRequestTopicName,
//                key = sagaId.toString(),
                key = payload.orderId,
                message = paymentRequestAvroModel,
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
                    log.info(
                        "Received successful response from Kafka for " +
                                "order id: $payload.orderId " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )

                    outboxCallback(
                        orderPaymentOutboxMessage,
                        OutboxStatus.COMPLETED
                    )
                } else {
                    log.error(
                        "Error while sending PaymentRequestAvroModel" +
                                "message $paymentRequestAvroModel " +
                                "to topic ${orderServiceConfigData.paymentRequestTopicName}",
                        ex
                    );

                    outboxCallback(
                        orderPaymentOutboxMessage,
                        OutboxStatus.FAILED
                    )
                }
            }

            log.info(
                "PaymentRequestAvroModel sent to Kafka " +
                        "for order id: ${payload.orderId} and " +
                        "saga id: ${orderPaymentOutboxMessage.sagaId}"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending PaymentRequestAvroModel message " +
                        "to kafka with " +
                        "order id: ${payload.orderId}, " +
                        "saga id: ${orderPaymentOutboxMessage.sagaId}" +
                        "error: ${ex.message}",
                ex
            )
        }
    }
}