package net.philipheur.food_ordering_system.payment_service.messaging.listener

import net.philipheur.food_ordering_system.common.domain.valueobject.PaymentOrderStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.consumer.KafkaConsumer
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentOrderStatus.CANCELLED
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentOrderStatus.PENDING
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentRequestAvroModel
import net.philipheur.food_ordering_system.payment_service.domain.service.dto.request.PaymentRequest
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.input.message.listener.PaymentRequestMessageListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class PaymentRequestKafkaListener(
    private val paymentRequestMessageListener: PaymentRequestMessageListener
) : KafkaConsumer<PaymentRequestAvroModel> {

    private val log by LoggerDelegator()

    @KafkaListener(
        id = "\${kafka-consumer-config.payment-consumer-group-id}",
        topics = ["\${payment-service.payment-request-topic-name}"]
    )
    override fun receive(
        @Payload models: List<PaymentRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "${models.size} number of payment requests received with " +
                    "keys:$keys, partitions:$partitions and offsets: $offsets",
        )

        models.forEach { model ->
            if (model.paymentOrderStatus == PENDING) {
                log.info(
                    "Processing payment " +
                            "for order id: ${model.orderId}"
                )
                paymentRequestMessageListener.completePayment(
                    modelToMessage(model)
                )
            } else if (model.paymentOrderStatus == CANCELLED) {
                log.info("Processing cancelling payment for order id: ${model.orderId}")
                paymentRequestMessageListener.cancelPayment(
                    modelToMessage(model)
                )
            }
        }
    }

    private fun modelToMessage(
        paymentRequestAvroModel: PaymentRequestAvroModel
    ) = PaymentRequest(
        id = paymentRequestAvroModel.id,
        sagaId = paymentRequestAvroModel.sagaId,
        orderId = paymentRequestAvroModel.orderId,
        customerId = paymentRequestAvroModel.customerId,
        price = paymentRequestAvroModel.price,
        createdAt = paymentRequestAvroModel.createdAt,
        paymentOrderStatus = PaymentOrderStatus.valueOf(
            paymentRequestAvroModel.paymentOrderStatus.name
        )
    )

}