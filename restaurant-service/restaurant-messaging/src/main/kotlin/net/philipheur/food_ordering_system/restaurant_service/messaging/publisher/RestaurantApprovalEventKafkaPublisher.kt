package net.philipheur.food_ordering_system.restaurant_service.messaging.publisher

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.OrderApprovalStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovedEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.service.config.RestaurantServiceConfigData
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.OrderApprovedMessagePublisher
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantApprovalEventKafkaPublisher(
    private val restaurantServiceConfigData: RestaurantServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalResponseAvroModel>
) : OrderApprovedMessagePublisher {

    private val log by LoggerDelegator()

    override fun publish(domainEvent: OrderApprovedEvent) {

        val orderId = domainEvent.orderApproval.orderId.value
        log.info(
            "Received OrderApprovalEvent for " +
                    "order id: $orderId"
        )

        try {
            val model = RestaurantApprovalResponseAvroModel(
                UUID.randomUUID(),
                UUID.randomUUID(),
                orderId,
                domainEvent.restaurantId.value,
                domainEvent.createdAt.toInstant(),
                OrderApprovalStatus.valueOf(domainEvent.orderApproval.orderApprovalStatus.name),
                domainEvent.failureMessages,
            )

            kafkaProducer.send(
                topicName = restaurantServiceConfigData.restaurantResponseTopicName,
                key = domainEvent.orderApproval.orderId.value.toString(),
                message = model,
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata
                    log.info(
                        "Received successful response from Kafka for " +
                                "order id: $orderId " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )
                } else {
                    log.error(
                        "Error while sending RestaurantApprovalResponseAvroModel" +
                                " message $model " +
                                "to topic ${restaurantServiceConfigData.restaurantResponseTopicName}",
                        ex
                    );
                }
            }
        } catch (e: Exception) {
            log.error(
                "Error while sending RestaurantApprovalResponseAvroModel message" +
                        " to kafka with order id: $orderId, error: ${e.message}"
            )
        }
    }
}