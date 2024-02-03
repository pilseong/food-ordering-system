package net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.Product
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalRequestAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantOrderStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.order_service.domain.application_service.config.OrderServiceConfigData
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderPaidRestaurantRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderPaidEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
open class OrderPaidKafkaMessagePublisher(
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalRequestAvroModel>
) : OrderPaidRestaurantRequestMessagePublisher {

    private val log by LoggerDelegator()

    override fun publish(domainEvent: OrderPaidEvent) {

        val orderId = domainEvent.order.id!!.value.toString()
        log.info("Received OrderPaidEvent for order id: {}", orderId)

        try {
            val order = domainEvent.order
            val restaurantApprovalRequestAvroModel = RestaurantApprovalRequestAvroModel(
                UUID.randomUUID(),
                UUID.randomUUID(),
                order.restaurantId.value,
                order.id!!.value,
                RestaurantOrderStatus
                    .valueOf(order.orderStatus!!.name),
                order.items.map {
                    Product(
                        it.id.toString(),
                        it.quantity
                    )
                },
                order.price.amount,
                domainEvent.createdAt.toInstant()
            )

            kafkaProducer.send(
                topicName = orderServiceConfigData.restaurantApprovalRequestTopicName,
                key = orderId,
                message = restaurantApprovalRequestAvroModel
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
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
                        "Error while sending " +
                                "message $restaurantApprovalRequestAvroModel " +
                                "to topic ${orderServiceConfigData.restaurantApprovalRequestTopicName}",
                        ex
                    );
                }
            }

            log.info(
                "RestaurantApprovalRequestAvroModel sent to Kafka " +
                        "for order id: ${restaurantApprovalRequestAvroModel.orderId}"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending RestaurantApprovalRequestAvroModel message to kafka with " +
                        "order id: $orderId, error: ${ex.message}",
                ex
            )
        }
    }
}