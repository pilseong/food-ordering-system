package net.philipheur.food_ordering_system.customer_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.customer_service.domain.application_service.config.CustomerServiceConfigData
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.output.message.publisher.CustomerMessagePublisher
import net.philipheur.food_ordering_system.customer_service.domain.core.event.CustomerCreatedEvent
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.CustomerAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import org.springframework.stereotype.Component

@Component
class CustomerCreatedEventKafkaPublisher(
    private val configData: CustomerServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, CustomerAvroModel>,
) : CustomerMessagePublisher {

    private val log by LoggerDelegator()
    override fun publish(customerCreatedEvent: CustomerCreatedEvent) {
        log.info(
            "Received CustomerCreatedEvent for " +
                    "customer id: ${customerCreatedEvent.customer.id!!.value}"
        )

        try {
            val customerAvroModel = CustomerAvroModel(
                customerCreatedEvent.customer.id!!.value,
                customerCreatedEvent.customer.username,
                customerCreatedEvent.customer.firstName,
                customerCreatedEvent.customer.lastName
            )

            kafkaProducer.send(
                topicName = configData.customerTopicName,
                key = customerAvroModel.id.toString(),
                message = customerAvroModel,
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
                    log.info(
                        "Received successful response from Kafka for " +
                                "customer id: ${customerAvroModel.id} " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )
                } else {
                    log.error(
                        "Error while sending CustomerAvroModel" +
                                "message $customerAvroModel " +
                                "to topic ${configData.customerTopicName}",
                        ex
                    )
                }
            }

            log.info(
                "CustomerAvroModel sent to Kafka " +
                        "for customer id: ${customerAvroModel.id} and "
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending CustomerAvroModel message " +
                        "to kafka with " +
                        "customer id: ${customerCreatedEvent.customer.id!!.value}, " +
                        "error: ${ex.message}",
                ex
            )
        }
    }
}