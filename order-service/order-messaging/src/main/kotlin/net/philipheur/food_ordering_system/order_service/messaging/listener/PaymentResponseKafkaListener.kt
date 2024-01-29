package net.philipheur.food_ordering_system.order_service.messaging.listener

import net.philipheur.food_ordering_system.infrastructure.kafka.consumer.KafkaConsumer
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentResponseAvroModel
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentStatus
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.PaymentStatus.COMPLETED
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.PaymentResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.PaymentResponseMessageListener
import net.philipheur.food_ordering_system.order_service.messaging.publisher.kafka.logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!


// 카프카에서 수신한 메시지를 domain 에서 정의한 Listener 인터페이스로 호출해 준다.
@Component
class PaymentResponseKafkaListener(
    private val paymentResponseMessageListener: PaymentResponseMessageListener,
) : KafkaConsumer<PaymentResponseAvroModel> {

    private val log = logger()
    override fun receive(
        models: List<PaymentResponseAvroModel>,
        keys: List<String>,
        partitions: List<Int>,
        offsets: List<Long>
    ) {
        log.info(
            "${models.size} number of payment responses received with " +
                    "keys:$keys, partitions:$partitions and offsets: $offsets",
        )

        models.forEach { model ->
            if (model.paymentStatus == COMPLETED) {
                log.info(
                    "Processing successful payment " +
                            "for order id: ${model.orderId}"
                )
                paymentResponseMessageListener.paymentCompleted(
                    modelToMessage(model)
                )
            } else if (
                model.paymentStatus == PaymentStatus.CANCELLED ||
                model.paymentStatus == PaymentStatus.FAILED
            ) {
                log.info("Processing unsuccessful payment for order id: ${model.orderId}", )
                paymentResponseMessageListener.paymentCancelled(
                    modelToMessage(model)
                )
            }
        }
    }

    private fun modelToMessage(msg: PaymentResponseAvroModel) =
        PaymentResponse(
            id = msg.id,
            sagaId = msg.sagaId,
            paymentId = msg.paymentId,
            customerId = msg.customerId,
            orderId = msg.orderId,
            price = msg.price,
            createdAt = msg.createdAt,
            paymentStatus =
            net.philipheur.food_ordering_system.common.domain.valueobject.PaymentStatus
                .valueOf(msg.paymentStatus.name),
            failureMessages = msg.failureMessages,
        )
}