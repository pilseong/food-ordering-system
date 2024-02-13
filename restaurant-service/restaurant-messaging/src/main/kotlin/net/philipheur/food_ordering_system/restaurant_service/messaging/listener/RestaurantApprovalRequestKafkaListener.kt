package net.philipheur.food_ordering_system.restaurant_service.messaging.listener

import net.philipheur.food_ordering_system.common.domain.valueobject.ProductId
import net.philipheur.food_ordering_system.common.domain.valueobject.RestaurantOrderStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.consumer.KafkaConsumer
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalRequestAvroModel
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.restaurant_service.domain.service.dto.RestaurantApprovalRequest
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.input.message.listener.RestaurantApprovalRequestMessageListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantApprovalRequestKafkaListener(
    private val messageListener:
    RestaurantApprovalRequestMessageListener,
) : KafkaConsumer<RestaurantApprovalRequestAvroModel> {

    private val log by LoggerDelegator()

    @KafkaListener(
        id = "\${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = ["\${restaurant-service.restaurant-approval-request-topic-name}"]
    )

    override fun receive(
        @Payload models: List<RestaurantApprovalRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "${models.size} number of restaurant approval requests received with " +
                    "keys:$keys, " +
                    "partitions:$partitions and " +
                    "offsets: $offsets",
        )

        // 수신한 식당 승인 요청 kafka 메시지를 도메인 객체로 변환하고 application service 에 통보한다.
        models.forEach { model ->
            log.info(
                "Processing restaurant approval " +
                        "for order id: ${model.orderId}"
            )
            // 비즈니스 로직으로 메시지 전달
            messageListener
                .approveOrder(
                    modelToMessage(model)
                )
        }
    }

    private fun modelToMessage(
        model: RestaurantApprovalRequestAvroModel
    ) = RestaurantApprovalRequest(
        id = model.id,
        sagaId = model.sagaId,
        restaurantId = model.restaurantId,
        orderId = model.orderId,
        restaurantOrderStatus = RestaurantOrderStatus
            .valueOf(model.restaurantOrderStatus.name),
        products = model.products.map {
            Product(
                productId = ProductId(UUID.fromString(it.id.toString())),
                quantity = it.quantity,
            )
        },
        price = model.price,
        createdAt = model.createdAt,
    )
}