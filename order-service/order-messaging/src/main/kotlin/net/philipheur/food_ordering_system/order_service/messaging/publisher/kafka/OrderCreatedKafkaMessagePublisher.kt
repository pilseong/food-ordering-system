package net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka


import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentOrderStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentRequestAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.producer.service.KafkaProducer
import net.philipheur.food_ordering_system.order_service.domain.application_service.config.OrderServiceConfigData
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.core.event.OrderCreatedEvent
import org.springframework.stereotype.Component
import java.util.*

/* domain 레이어에서 사용할 publisher 의 기능을 실제 구현하는 구현체 클래스 */
@Component
open class CreateOrderKafkaMessagePublisher(
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>
) : OrderCreatedPaymentRequestMessagePublisher {

    private val log by LoggerDelegator()

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
                topicName = orderServiceConfigData.paymentRequestTopicName,
                key = orderId,
                message = paymentRequestAvroModel
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
                                "message $paymentRequestAvroModel " +
                                "to topic ${orderServiceConfigData.paymentRequestTopicName}",
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