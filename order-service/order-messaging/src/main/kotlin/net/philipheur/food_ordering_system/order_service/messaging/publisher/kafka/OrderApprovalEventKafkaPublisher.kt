package net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.Product
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalRequestAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantOrderStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.order_service.domain.application_service.config.OrderServiceConfigData
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.model.approval.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.messaging.mapper.KafkaMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderApprovalEventKafkaPublisher(
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalRequestAvroModel>,
    private val kafkaMessageHelper: KafkaMessageHelper,
) : RestaurantApprovalRequestMessagePublisher {

    private val log by LoggerDelegator()
    override fun publish(
        orderApprovalOutboxMessage: OrderApprovalOutboxMessage,
        outboxCallback: (OrderApprovalOutboxMessage, OutboxStatus) -> Unit
    ) {
        val payload = kafkaMessageHelper.getOrderEventPayload(
            payload = orderApprovalOutboxMessage.payload,
            outputType = OrderApprovalEventPayload::class.java
        )

        val sagaId = orderApprovalOutboxMessage.sagaId

        log.info(
            "Received OrderApprovalOutboxMessage for " +
                    "order id: {}", payload.orderId
        )

        try {
            val avroModel = RestaurantApprovalRequestAvroModel(
                UUID.randomUUID(),
                sagaId,
                UUID.fromString(payload.restaurantId),
                UUID.fromString(payload.orderId),
                RestaurantOrderStatus
                    .valueOf(orderApprovalOutboxMessage.orderStatus.name),
                payload.products.map {
                    Product(
                        it.id,
                        it.quantity
                    )
                },
                payload.price,
                orderApprovalOutboxMessage.createdAt.toInstant()
            )

            kafkaProducer.send(
                topicName = orderServiceConfigData
                    .restaurantApprovalRequestTopicName,
                key = sagaId.toString(),
                message = avroModel,
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
                        orderApprovalOutboxMessage,
                        OutboxStatus.COMPLETED
                    )
                } else {
                    log.error(
                        "Error while sending RestaurantApprovalRequestAvroModel" +
                                "message $orderApprovalOutboxMessage " +
                                "to topic ${
                                    orderServiceConfigData
                                        .restaurantApprovalRequestTopicName
                                }",
                        ex
                    );

                    outboxCallback(
                        orderApprovalOutboxMessage,
                        OutboxStatus.FAILED
                    )
                }
            }

            log.info(
                "RestaurantApprovalRequestAvroModel sent to Kafka " +
                        "for order id: ${payload.orderId} and" +
                        "saga id: $sagaId"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending RestaurantApprovalRequestAvroModel message " +
                        "to kafka with " +
                        "order id: ${payload.orderId}, " +
                        "saga id: ${orderApprovalOutboxMessage.sagaId}" +
                        "error: ${ex.message}",
                ex
            )
        }

    }
}