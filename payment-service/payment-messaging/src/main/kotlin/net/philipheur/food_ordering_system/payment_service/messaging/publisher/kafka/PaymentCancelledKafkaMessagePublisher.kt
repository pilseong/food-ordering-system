package net.philipheur.food_ordering_system.payment_service.messaging.publisher.kafka

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.payment_service.domain.core.event.PaymentCancelledEvent
import net.philipheur.food_ordering_system.payment_service.domain.service.config.PaymentServiceConfigData
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentCancelledMessagePublisher
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentCancelledKafkaMessagePublisher(
    private val paymentServiceConfigData: PaymentServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, PaymentResponseAvroModel>,
) : PaymentCancelledMessagePublisher {

    private val log by LoggerDelegator()

    override fun publish(domainEvent: PaymentCancelledEvent) {
        val orderId = domainEvent.payment.orderId.value.toString()
        val payment = domainEvent.payment

        log.info("Received PaymentCancelledEvent for order id: $orderId")

        try {
            val paymentResponseAvroModel = PaymentResponseAvroModel(
                UUID.randomUUID(),
                payment.orderId.value,  // 임시로
                payment.id!!.value,
                payment.customerId.value,
                payment.orderId.value,
                payment.price.amount,
                domainEvent.createAt.toInstant(),
                PaymentStatus.valueOf(payment.paymentStatus!!.name),
                domainEvent.failureMessages
            )

            kafkaProducer.send(
                topicName = paymentServiceConfigData.paymentResponseTopicName,
                key = orderId,
                message = paymentResponseAvroModel
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
                }
            }
        } catch (ex: Exception) {
            log.error(
                "Error while sending PaymentResponseAvroModel message to kafka with " +
                        "order id: $orderId, error: ${ex.message}",
                ex
            )
        }
    }
}