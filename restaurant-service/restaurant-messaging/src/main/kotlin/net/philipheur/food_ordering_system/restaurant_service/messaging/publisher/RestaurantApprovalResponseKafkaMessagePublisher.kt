package net.philipheur.food_ordering_system.restaurant_service.messaging.publisher

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.restaurant_service.domain.service.config.RestaurantServiceConfigData
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.RestaurantApprovalResponseMessagePublisher
import net.philipheur.food_ordering_system.restaurant_service.messaging.mapper.KafkaMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantApprovalResponseKafkaMessagePublisher(
    private val configData: RestaurantServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalResponseAvroModel>,
    private val kafkaMessageHelper: KafkaMessageHelper,
) :
    RestaurantApprovalResponseMessagePublisher {

    private val log by LoggerDelegator()
    override fun publish(
        outboxMessage: OrderApprovalOutboxMessage,
        outboxCallback: (OrderApprovalOutboxMessage, OutboxStatus) -> Unit
    ) {
        val payload = kafkaMessageHelper.getEventPayload(
            payload = outboxMessage.payload,
            outputType = OrderApprovalEventPayload::class.java
        )

        val sagaId = outboxMessage.sagaId

        log.info(
            "Received ${outboxMessage.orderApprovalStatus} OrderApprovalOutboxMessage for " +
                    "order id: {}", payload.orderId
        )

        try {
            val avroModel = RestaurantApprovalResponseAvroModel(
                UUID.randomUUID(),
                sagaId,
                UUID.fromString(payload.restaurantId),
                UUID.fromString(payload.orderId),
                payload.createdAt.toInstant(),
                OrderApprovalStatus.valueOf(payload.orderApprovalStatus),
                payload.failureMessages
            )

            // 키는 sagaId로 처리해야 같은 partition 에서 처리 가능하다.
            kafkaProducer.send(
                topicName = configData.restaurantApprovalResponseTopicName,
                key = outboxMessage.sagaId.toString(),
                message = avroModel,
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
                    log.info(
                        "Received ${outboxMessage.orderApprovalStatus} successful response " +
                                "from Kafka for " +
                                "order id: $payload.orderId " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )

                    // outbox 메시지 종결 처리
                    outboxCallback(
                        outboxMessage,
                        OutboxStatus.COMPLETED
                    )
                } else {
                    log.error(
                        "Error while sending RestaurantApprovalResponseAvroModel" +
                                "message $avroModel " +
                                "to topic ${configData.restaurantApprovalResponseTopicName}",
                        ex
                    );

                    // outbox 메시지 종결 처리
                    outboxCallback(
                        outboxMessage,
                        OutboxStatus.FAILED
                    )
                }
            }

            log.info(
                "PaymentResponseAvroModel sent to Kafka " +
                        "for order id: ${payload.orderId} and " +
                        "saga id: ${outboxMessage.sagaId}"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending RestaurantApprovalResponseAvroModel message " +
                        "to kafka with " +
                        "order id: ${payload.orderId}, " +
                        "saga id: ${outboxMessage.sagaId}" +
                        "error: ${ex.message}",
                ex
            )
        }
    }
}