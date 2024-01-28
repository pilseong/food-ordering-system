package net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka


import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentOrderStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentRequestAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.order_service.domain.application_service.OrderServiceConfigData
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

@Component
open class CreateOrderKafkaMessagePublisher(
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>
) : OrderCreatedPaymentRequestMessagePublisher {

    private val log = logger()

    override fun publish(domainEvent: OrderCreatedEvent) {

        val orderId = domainEvent.order.id!!.value.toString()
        log.info("Received OrderCreatedEvent for order id: {}", orderId)

        try {
            val order = domainEvent.order
            val paymentRequestAvroModel = PaymentRequestAvroModel(
                UUID.randomUUID(),
                UUID.randomUUID(),
                order.customerId.value,
                order.id!!.value,
                order.price.amount,
                domainEvent.createdAt.toInstant(),
                PaymentOrderStatus.PENDING
            )

            kafkaProducer.send(
                orderServiceConfigData.paymentRequestTopicName!!,
                orderId,
                paymentRequestAvroModel
            ) { result, ex ->
                if (ex == null) {
                    val metadata = result!!.recordMetadata;
                    log.info(
                        "Received new metadata. " +
                                "Topic: ${metadata.topic()}; " +
                                "Partition: ${metadata.partition()}; " +
                                "Offset: ${metadata.offset()}; " +
                                "TimeStamp: ${metadata.timestamp()};, " +
                                "at time: ${System.nanoTime()}"
                    )
                } else {
                    log.error(
                        "Error while sending message {} to topic {}",
                        paymentRequestAvroModel.toString(),
                        orderServiceConfigData.paymentResponseTopicName,
                        ex
                    );
                }
            }

            log.info(
                "PaymentRequestAvroModel sent to Kafka " +
                        "for order id: ${paymentRequestAvroModel.orderId}"
            )
        } catch (ex: Exception) {
            log.error(
                "Error while sending PaymentRequestAvroModel message to kafka with " +
                        "order id: $orderId, error: ${ex.message}",
                ex
            )
        }
    }
}