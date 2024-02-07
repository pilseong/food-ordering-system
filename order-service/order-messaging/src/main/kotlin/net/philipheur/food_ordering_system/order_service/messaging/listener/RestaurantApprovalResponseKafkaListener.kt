package net.philipheur.food_ordering_system.order_service.messaging.listener

import net.philipheur.food_ordering_system.common.domain.valueobject.OrderApprovalStatus
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.kafka.consumer.KafkaConsumer
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.OrderApprovalStatus.APPROVED
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.OrderApprovalStatus.REJECTED
import net.philipheur.food_ordering_system.infrastructure.kafka.model.avro.order.RestaurantApprovalResponseAvroModel
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.message.RestaurantApprovalResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.message.RestaurantApprovalResponseMessageListener
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderNotFoundException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
open class RestaurantApprovalResponseKafkaListener(
    private val messageListener: RestaurantApprovalResponseMessageListener
) : KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private val log by LoggerDelegator()


    @KafkaListener(
        id = "\${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = ["\${order-service.restaurant-approval-response-topic-name}"]
    )
    override fun receive(
        @Payload models: List<RestaurantApprovalResponseAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "${models.size} number of restaurant approval responses " +
                    "received with " +
                    "keys $keys, " +
                    "partitions $partitions and " +
                    "offsets $offsets"
        )

        models.forEach { model ->

            try {
                if (model.orderApprovalStatus == APPROVED) {
                    log.info(
                        "Processing approved order for " +
                                "order id: ${model.orderId}",

                        )

                    // 도메인 로직으로 메시지를 전달한다.
                    messageListener.orderApproved(
                        response = RestaurantApprovalResponse(
                            id = model.id,
                            sagaId = model.sagaId,
                            restaurantId = model.restaurantId,
                            orderId = model.orderId,
                            createdAt = model.createdAt,
                            orderApprovalStatus = OrderApprovalStatus
                                .valueOf(model.orderApprovalStatus.name),
                            failureMessages = model.failureMessages
                        )
                    )
                } else if (model.orderApprovalStatus == REJECTED) {
                    log.info(
                        "Processing rejected order for " +
                                "order id: ${model.orderId}, " +
                                "with failure messages: ${
                                    model.failureMessages
                                        .joinToString(",")
                                }",

                        )
                    // 거절 메시지 처리
                    messageListener.orderRejected(
                        response = RestaurantApprovalResponse(
                            id = model.id,
                            sagaId = model.sagaId,
                            restaurantId = model.restaurantId,
                            orderId = model.orderId,
                            createdAt = model.createdAt,
                            orderApprovalStatus = OrderApprovalStatus
                                .valueOf(model.orderApprovalStatus.name),
                            failureMessages = model.failureMessages
                        )
                    )
                }
            } catch (e: OptimisticLockingFailureException) {
                log.error(
                    "Caught optimistic locking exception in " +
                            "RestaurantApprovalResponseKafkaListener for " +
                            "order id: ${model.orderId}",

                    )
            } catch (e: OrderNotFoundException) {
                log.error("No order found for order id: ${model.orderId}")
            }
        }

    }

}